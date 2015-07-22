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
    		success: function(response) { self.onDeleteSuccess(response, callback); },
    		error: function(req, resp) { self.onDeleteError(null, callback); }
    	});
    },
    onDeleteSuccess: function(response, callback) {
    	if(response.error) return this.onDeleteError(response.error);
		$(this.el).remove();
		if(callback) callback();
    },
    onDeleteError: function(message, callback) {
    	if(!message) message = "{{generic.js.error}}".format("{{generis.js.deleting}}", "{{generic.js.thecomment}}");
		app.pushMessageAndNavigate("error", message);
		if(callback) callback();
    }
});

window.CommentListView = Backbone.View.extend({
	page: 1,
	stopScroll: false,
	itemViews: {},

	initialize: function() {
		var self = this;
		viewLoader.load("CommentListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template());
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
				success: function(comments) { return self.onFetchSuccess(comments); },
				error: function () { self.onFetchError(); }
			});
			this.page++;
		}
	},
	onFetchSuccess: function(comments) {
		if(comments.error) return this.onFetchError(comments.error);
		var self = this;
		if(comments.models.length == 0) self.stopScroll = true;
		_.each(comments.models, function (comment) {
			var itemView = new CommentListItemView({comment: comment, parentViewName: this.lastOptions.parentViewName});
			self.itemViews[comment.id] = itemView;
			$('table tbody', self.el).append(itemView.render().el);
		}, self);
		return self;
	},
	onFetchError: function(message) {
		$(self.el).empty();
		$(self.el).html(self.template());
		return self;
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
	events: {
		'submit .edit-comment-form': 'saveComment'
	},
	saveComment: function (ev) {
		var commentDetails = $(ev.currentTarget).serializeObject();
		var comment = new Comment({stream:commentDetails.parentStream});
		var self = this;
		comment.save(commentDetails, {
			success: function (comment) { self.onSaveSuccess(comment); },
			error: function (req, resp) { self.onSaveError(); }
		});
		return false;
	},
	onSaveSuccess: function(comment) {
		if(comment.error) return this.onSaveError(comment.error);
		if(this.lastOptions && this.lastOptions.parentView)
			this.lastOptions.parentView.toggleEdit(comment);
		else
			app.rerenderView();
	},
	onSaveError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.saving}}", "{{generic.js.thecomment}}");
		app.pushMessageAndNavigate("error", message);
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.comment = new Comment(options);
			self.comment.fetch({
				success: function (comment) { return self.onFetchSuccess(comment); },
				error: function () { self.onFetchError(); }
			});
		} else {
			this.onFetchSuccess();
		}
		return this;
	},
	onFetchSuccess: function(comment) {
		if(comment && comment.error) return this.onFetchError(comment.error);
		$(this.el).empty();
		$(this.el).html(this.template({comment: comment, stream: this.lastOptions.stream}));
		return self;
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.thecomment}}");
		app.pushMessageAndNavigate("error", message);
	}
});
