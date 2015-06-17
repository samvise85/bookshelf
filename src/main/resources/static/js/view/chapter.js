window.ChapterListItemView = Backbone.View.extend({
    tagName:"tr",

    initialize:function (chapter) {
		this.model = chapter;
        this.model.bind("change", this.render, this);
        this.model.bind("destroy", this.close, this);
    },

    render:function () {
        $(this.el).html(this.template({chapter: this.model}));
        return this;
    }
});

window.ChapterListView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	page: 1,
	stopScroll: false,
	
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template({view: this}));
		this.append(options);
	},
	append: function (options) {
		if(!(this.stopScroll === true)) {
			var self = this;
			var chapters = new Chapters({book: options.book, page: this.page});
			chapters.fetch({
				success: function(chapters) {
					//console.log(chapters.models.length);
					//console.log(chapters.models.length == 0);
					if(chapters.models.length == 0) self.stopScroll = true;
					_.each(chapters.models, function (chapter) {
						$('table tbody', self.el).append(new ChapterListItemView(chapter).render().el);
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
	}
});

window.ChapterSelectItemView = Backbone.View.extend({
    render:function (options) {
        var html = this.template(options);
        this.setElement(html);
        return this;
    }
});

window.ChapterEditView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
		'submit .edit-chapter-form': 'saveChapter',
		'click .delete': 'deleteChapter'
	},
	saveChapter: function (ev) {
		var chapterDetails = $(ev.currentTarget).serializeObject();
		var chapter = new Chapter({book:chapterDetails.book});
		chapter.save(chapterDetails, {
			success: function (chapter) {
				if(app.bookReadView) app.bookReadView.reload = true;
				app.navigate('book/' + chapter.get('book'), {trigger:true});
			},
			error: function (req, resp) {
				if(resp.status == 200) {
					if(app.bookReadView) app.bookReadView.reload = true;
					app.navigate('book/' + chapter.get('book'), {trigger:true, reload:true});
				}
			}
		});
		return false;
	},
	deleteChapters: function (ev) {
		var book = this.chapter.get('book');
		var title = this.chapter.get('title');
		this.chapter.destroy({
			success: function () {
				if(app.bookReadView) app.bookReadView.reload = true;
				app.navigate('book/' + book, {trigger:true});
			},
			error: function () {
				app.messageView.errors.push("An error occurred deleting " + htmlEncode(title));
				app.messageView.rerender();
			}
		});
		return false;
	},
	render: function (options) {
		lastOptions = options;
		var self = this;
		if(options.id) {
			self.chapter = new Chapter(options);
			self.chapter.fetch({
				success: function (chapter) {
					$(self.el).empty();
					$(self.el).html(self.template({book: options.book, chapter: chapter}));
					self.showChapterSelection(options.book);
					return self;
				},
				error: function () {
					app.messageView.errors.push("This is not the chapter you're looking for.");
					app.messageView.rerender();
				}
			});
		} else {
			$(self.el).empty();
			$(self.el).html(self.template({book: options.book, chapter: null}));
			self.showChapterSelection(options.book);
			return self;
		}
	},
	showChapterSelection: function(book) {
		var self = this;
		var chapters = new Chapters({book: book});
		chapters.fetch({
			success: function(chapters) {
				if(chapters.models.length > 0) {
					_.each(chapters.models, function (chapter) {
						var selectItemView = new ChapterSelectItemView().render({chapter: chapter, current: self.chapter.id});
						$('select[name=position]').append(selectItemView.el);
					});
				}
			}
		});
	}
});

window.ChapterReadView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
		'click .prev': 'prev',
		'click .next': 'next'
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.chapter = new Chapter(options);
			self.chapter.fetch({
				success: function (chapter) {
					$(self.el).empty();
					$(self.el).html(self.template({book: options.book, chapter: chapter}));
					self.loadComments();
					return self;
				},
				error: function () {
					app.messageView.errors.push("This is not the chapter you're looking for.");
					app.messageView.rerender();
				}
			});
		} else if(options.position) {
			self.chapter = new ChapterByPosition(options);
			self.chapter.fetch({
				success: function (chapter) {
					$(self.el).empty();
					$(self.el).html(self.template({book: options.book, chapter: chapter}));
					self.loadComments();
					return self;
				},
				error: function () {
					app.messageView.errors.push("This is not the chapter you're looking for.");
					app.messageView.rerender();
				}
			});
		} else {
			app.messageView.errors.push("This is not the chapter you're looking for.");
			app.messageView.rerender();
		}
	},
	prev: function () {
		var position;
		if(this.chapter)
			position = parseInt(this.chapter.get('position'))-1;
		if(position > 0) {
			this.navToChapterPosition(position ? position : null);
		} else {
			app.messageView.errors.push("Previously on " + this.chapter.get('book') + '... Nothing happened.');
			app.messageView.rerender();
		}
	},
	scroll: function () {
		var self = this;
		if(!self.next)
			self = app.chapterReadView;
		self.next();
	},
	next: function () {
		var position;
		if(this.chapter) {
			position = parseInt(this.chapter.get('position'))+1;
			this.navToChapterPosition(position ? position : null);
		}
	},
	navToChapterPosition: function(position) {
		if(this.chapter && position && position != null) {
			app.navigate('book/' + this.chapter.get('book') + '/chapter?position=' + position, {trigger:true});
		} else {
			app.messageView.errors.push("1-1-3-7-... what was that again... ?");
			app.messageView.rerender();
		}
	},
	loadComments: function() {
		if(!this.commentListView) {
			this.commentListView = new CommentListView();
			this.commentListView.render({stream: this.chapter.get('stream'), parentViewName: 'chapterReadView'});
			$("#chapter"+this.chapter.get('id')+" .comments").html(this.commentListView.el);
		} else {
			this.commentListView.append({stream: this.chapter.get('stream')});
		}
		
	}
});
