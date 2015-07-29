window.ChapterListItemView = window.ListItemView.extend({});

window.ChapterListView = window.ListView.extend({
	itemClassName: "ChapterListItemView",
	ItemClass: ChapterListItemView,
	tableClass: ".chapters_table",
	ModelClass: Chapters,
	
	getFetchOptions: function(options) {
		return {book: options.book};
	}
});

window.ChapterSelectItemView = Backbone.View.extend({
    render: function(options) {
        var html = this.template(options);
        this.setElement(html);
        return this;
    }
});

window.ChapterEditView = Backbone.View.extend({
	reload : false,
	clearMessages : true,
	messages: new Messages(true),
	
	initialize: function() {
		var self = this;
		viewLoader.load("ChapterSelectItemView", function() {
			self.listItemViewReady = true;
		});
	},
	events: {
		'submit .edit-chapter-form': 'saveChapter',
		'blur [name=title]': 'validateTitle',
		'blur [name=number]': 'validateNumber',
	},
	validateTitle: function() {
		this.messages.remove("title_err");
		var title = $('[name=title]').val();
		if(title == null || title.isEmpty())
			this.messages.add("title_err", "{{chapter.js.titleerr}}");
	},
	validateNumber: function() {
		this.messages.remove("number_err");
		var number = $('[name=number]').val();
		if(number == null || number.isEmpty())
			this.messages.add("number_err", "{{chapter.js.numbererr}}");
	},
	validate: function() {
		this.validateTitle();
		this.validateNumber();
	},
	saveChapter: function (ev) {
		this.validate();
		if(this.messages.isEmpty()) {
			var chapterDetails = $(ev.currentTarget).serializeObject();
			var chapter = new Chapter({book:chapterDetails.book});
			var self = this;
			chapter.save(chapterDetails, {
				success: function (chapter) { self.onSaveSuccess(chapter); },
				error: function (req, resp) { self.onSaveError(); }
			});
		}
		return false;
	},
	onSaveSuccess: function(chapter) {
		if(chapter.error || chapter.errorMap) return this.onSaveError(chapter.error, chapter.errorMap);
		app.navigate('book/' + chapter.get('book'), {trigger:true});
	},
	onSaveError: function(message, errorMap) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.saving}}", "{{generic.js.thechapter}}");
		app.pushMessageAndNavigate("error", message);
		//errorMapToMessages(errorMap);//TODO
	},
	deleteChapter: function (callback) {
		var book = this.chapter.get('book');
		var title = this.chapter.get('title');
		var self = this;
		this.chapter.destroy({
			success: function (response) { self.onDeleteSuccess(response, title, book, callback); },
			error: function (req, resp) { self.onDeleteError(null, callback); }
		});
		return false;
	},
	onDeleteSuccess: function(response, title, book, callback) {
		if(response.error) return this.onDeleteError(response.error, callback);
		app.pushMessageAndNavigate("message", "{{chapter.js.deleted}}".format(title), "book/" + book);
		if(callback) callback();
	},
	onDeleteError: function(message, callback) {
		if(!message) message = "{{generic.js.error}}".format("{{generis.js.deleting}}", htmlEncode(title));
		app.pushMessageAndNavigate("error", message);
		if(callback) callback();
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.chapter = new Chapter(options);
			self.chapter.fetch({
				success: function (chapter) { self.onFetchSuccess(chapter); },
				error: function () { self.onFetchError(); }
			});
		} else {
			self.onFetchSuccess(null);
		}
	},
	onFetchSuccess: function(chapter) {
		if(chapter && chapter.error) return this.onFetchError(chapter.error);
		$(this.el).empty();
		$(this.el).html(this.template({book: this.lastOptions.book, chapter: chapter}));
		this.showChapterSelection(this.lastOptions.book);
		return this;
		
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.thechapter}}");
		app.pushMessageAndNavigate("error", message);
	},
	showChapterSelection: function(book) {
		if(this.listItemViewReady) {
			this.showChapterSelectionWhenReady(book);
		} else {
			var self  = this;
			setTimeout(function() { self.showChapterSelection(book); }, 100);
		}
	},
	showChapterSelectionWhenReady: function(book) {
		var self = this;
		var chapters = new Chapters({book: book});
		chapters.fetch({
			success: function(chapters) {
				if(chapters.models.length > 0) {
					_.each(chapters.models, function (chapter) {
						var current = null;
						if(self.chapter) current = self.chapter.id;
						var selectItemView = new ChapterSelectItemView().render({chapter: chapter, current: current});
						$('select[name=position]').append(selectItemView.el);
					});
				}
			}
		});
	}
});

window.ChapterReadView = Backbone.View.extend({
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
				success: function (chapter) { self.onFetchSuccess(chapter); },
				error: function () { self.onFetchError(); }
			});
		} else if(options.title) {
			self.chapter = new Chapter(options);
			self.chapter.fetch({
				data: $.param({"title":options.title, "pos":options.position}),
				success: function (chapter) { self.onFetchSuccess(chapter); },
				error: function () { self.onFetchError(); }
			});
		} else if(options.position) {
			self.chapter = new ChapterByPosition(options);
			self.chapter.fetch({
				data: $.param({"position": options.position}),
				success: function (chapter) { self.onFetchSuccess(chapter); },
				error: function () { self.onFetchError(); }
			});
		} else {
			this.onFetchError();
		}
	},
	onFetchSuccess: function(chapter) {
		if(!chapter.id || chapter.error) return this.onFetchError(chapter.error);
		$(this.el).empty();
		$(this.el).html(this.template({book: this.lastOptions.book, chapter: chapter}));
		this.loadComments();
		return this;
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.thechapter}}");
		app.pushMessageAndNavigate("error", message);
	},
	prev: function () {
		var position;
		if(this.chapter)
			position = parseInt(this.chapter.get('position'))-1;
		if(position > 0) {
			this.navToChapterPosition(position ? position : null);
		} else {
			this.onFetchError("{{chapter.js.noprev}}".format(this.chapter.get('book')));
		}
	},
	next: function () {
		var position;
		if(this.chapter) {
			position = parseInt(this.chapter.get('position'))+1;
			this.navToChapterPosition(position ? position : null);
		}
	},
	navToChapterPosition: function(position) {
		if(this.chapter && position && position != null)
			app.navigate('book/' + this.chapter.get('book') + '/chapter?position=' + position, {trigger:true});
		else
			this.onFetchError("{{chapter.js.noposition}}");
	},
	loadComments: function() {
		if(!this.commentListView) {
			var self = this;
			viewLoader.load("CommentListView", function() {
				self.commentListView = new CommentListView();
				self.commentListView.render({stream: self.chapter.get('stream'), parentViewName: 'chapterReadView'});
				$("#chapter"+self.chapter.get('id')+" .comments").html(self.commentListView.el);
			})
		} else {
			this.commentListView.append({stream: this.chapter.get('stream')});
		}
	}
});
