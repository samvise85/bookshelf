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
	num: null,
	stopScroll: false,
	
	initialize: function() {
		var self = this;
		viewLoader.load("ChapterListItemView", function() {
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
		if(options.parentElement)
			options.parentElement.html(this.el);
		pageHeight = $(window).height();
//		console.log("pageHeight: " + pageHeight);
//		console.log("offset: " + $(this.el).find('.chapters_table').offset().top);
		maxh = pageHeight- $(this.el).find('.chapters_table').offset().top;
		
		$(this.el).find('.chapters_table').css("max-height", maxh + "px");
		this.num = parseInt(parseInt(maxh)/(app.isAdmin() ? 47 : 37)) + 5;
		this.append(options);
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
			var chapters = new Chapters({book: options.book});
			chapters.fetch({
				data: $.param({"page": self.page, "num": self.num}),
				success: function(chapters) {
					if(chapters.models.length == 0 || chapters.models.length < self.num) self.stopScroll = true;
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
	messages: new Messages(true),
	
	initialize: function() {
		var self = this;
		viewLoader.load("ChapterSelectItemView", function() {
			self.listItemViewReady = true;
		});
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
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
//		console.log("messages empty: " + this.messages.isEmpty());
		if(this.messages.isEmpty()) {
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
		}
		return false;
	},
	deleteChapter: function (callback) {
		var book = this.chapter.get('book');
		var title = this.chapter.get('title');
		var self = this;
		this.chapter.destroy({
			success: function () {
				self.backToBook(title, book);
				if(callback) callback();
			},
			error: function (req, resp) {
				if(resp.status == 200)
					self.backToBook(book);
				else
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generis.js.deleting}}", htmlEncode(title)));
				if(callback) callback();
			}
		});
		return false;
	},
	backToBook: function(title, book) {
		if(app.bookReadView) app.bookReadView.reload = true;
		app.pushMessageAndNavigate("message", "{{chapter.js.deleted}}".format(title), "book/" + book);
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
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thechapter}}"));
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
//			data: $.param({"projection": "MAX"}),
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
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thechapter}}"));
				}
			});
		} else if(options.title) {
			self.chapter = new Chapter(options);
			self.chapter.fetch({
				data: $.param({"title":options.title, "pos":options.position}),
				success: function (chapter) {
					$(self.el).empty();
					$(self.el).html(self.template({book: options.book, chapter: chapter}));
					self.loadComments();
					return self;
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thechapter}}"));
				}
			});
		} else if(options.position) {
			self.chapter = new ChapterByPosition(options);
			self.chapter.fetch({
				data: $.param({"position": options.position}),
				success: function (chapter) {
					$(self.el).empty();
					$(self.el).html(self.template({book: options.book, chapter: chapter}));
					self.loadComments();
					return self;
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thechapter}}"));
				}
			});
		} else {
			app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thechapter}}"));
		}
	},
	prev: function () {
		var position;
		if(this.chapter)
			position = parseInt(this.chapter.get('position'))-1;
		if(position > 0) {
			this.navToChapterPosition(position ? position : null);
		} else {
			app.pushMessageAndNavigate("error", "{{chapter.js.noprev}}".format(this.chapter.get('book')));
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
			app.pushMessageAndNavigate("error", "{{chapter.js.noposition}}");
		}
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
