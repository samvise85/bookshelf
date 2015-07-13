window.CommentListItemView = Backbone.View.extend({
    tagName:"tr",

	events: {
		'click .edit': 'toggleEdit'
	},
    initialize:function (options) {
		this.options = options;
        this.options.comment.bind("change", this.render, this);
        this.options.comment.bind("destroy", this.close, this);
    },
    onClose: function(){
    	this.options.comment.unbind("change", this.render);
        this.options.comment.unbind("destroy", this.close);
    },

    render:function () {
        $(this.el).html(this.template(this.options));
        return this;
    },
    toggleEdit: function (e) {
    	if(e && e.currentTarget) { //click pulsante edit
	    	e.preventDefault();
	    	var oldHtml = $(e.currentTarget.parentElement).html();
	    	var self = this;
	    	viewLoader.load("CommentEditView", function() {
		    	var commEdit = new CommentEditView();
		    	commEdit.render({id: self.options.comment.id, stream: self.options.comment.get('stream'), parentView: self});
		    	$(e.currentTarget.parentElement).html(commEdit.el);
//		    	console.log('CommentListItemView::toggleEdit(' + e.currentTarget.id + ")");
	    	});
    	} else { //modifica effettuata
//	    	console.log('saveComment::toggleEdit(' + e + ")");
	    	var oldEl = this.el;
    		this.options.comment = e;
    		this.render();
    		$(oldEl).replaceWith(this.el);
    	}
    },
    deleteComment: function(callback) {
    	var self = this;
    	this.options.comment.destroy({
    		success: function() {
    			$(self.el).remove();
    			if(callback) callback();
    		},
    		error: function(req, resp) {
				if(resp.status == 200) {
	    			$(self.el).remove();
				} else {
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generis.js.deleting}}", "{{generic.js.thecomment}}"));
				}
				if(callback) callback();
    		}
    	});
    }
});

window.CommentListView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	page: 1,
	stopScroll: false,
	itemViews: {},

	initialize: function() {
		var self = this;
		viewLoader.load("CommentListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template({view: this}));
		this.append(options);
		if(app.getUser()) {
			var self = this;
	    	viewLoader.load("CommentEditView", function() {
				commEdit = new CommentEditView().render(options);
				$('.comment_area',self.el).html(commEdit.el);
	    	});
		}
	},
	append: function (options) {
		if(this.listItemViewReady) {
			this.appendWhenReady(options);
		} else {
			var self  = this;
			setTimeout(function() { self.append(options); }, 100);
		}
	},
	appendWhenReady: function (options) {
		if(!(this.stopScroll === true)) {
			var self = this;
			var comments = new Comments({stream: options.stream, page: this.page});
			comments.fetch({
				success: function(comments) {
					if(comments.models.length == 0) self.stopScroll = true;
					_.each(comments.models, function (comment) {
						var itemView = new CommentListItemView({comment: comment, parentViewName: options.parentViewName});
						self.itemViews[comment.id] = itemView;
						$('table tbody', self.el).append(itemView.render().el);
					}, self);
					return self;
				},
				error: function () {
					$(self.el).empty();
					$(self.el).html(self.template());
					return self;
				}
			});
			this.page++;
		}
	},
	toggleEdit: function (options) {
		alert('CommentListView::toggleEdit(' + options + ')');
	},
	deleteComment: function(source, callback) {
		var itemView = this.itemViews[source.parent().attr('id').split('-')[1]];
		if(itemView) itemView.deleteComment(callback);
	}
});

window.CommentEditView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
		'submit .edit-comment-form': 'saveComment'
	},
	saveComment: function (ev) {
		var commentDetails = $(ev.currentTarget).serializeObject();
		var comment = new Comment({stream:commentDetails.parentStream});
		comment.save(commentDetails, {
			success: function (comment) {
				if(lastOptions && lastOptions.parentView) {
					lastOptions.parentView.toggleEdit(comment);
				} else {
					app.rerenderView();
				}
			},
			error: function (req, resp) {
				if(resp.status != 403) {
					app.rerenderView();
				}
			}
		});
		return false;
	},
	render: function (options) {
		lastOptions = options;
		var self = this;
		if(options.id) {
			self.comment = new Comment(options);
			self.comment.fetch({
				success: function (comment) {
					$(self.el).empty();
					$(self.el).html(self.template({comment: comment, stream: options.stream}));
					return self;
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thecomment}}"));
				}
			});
		} else {
			$(self.el).empty();
			$(self.el).html(self.template({stream: options.stream, comment: null}));
			return self;
		}
	}
});
