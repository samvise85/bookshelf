
window.BookListView = Backbone.View.extend({
	reload : false,
	clearMessages : false,
	render: function () {
		var self = this;
		var books = new Books();
		books.fetch({
			success: function(books) {
				$(self.el).empty();
				$(self.el).html(self.template({books: books.models}));
				return self;
			},
			error: function () {
				app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.loading}}", "{{generic.js.booklist}}"));
			}
		});
	}
});

window.BookEditView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
		'submit .edit-book-form': 'saveBook',
//		'click .delete': 'deleteBook'
	},
	saveBook: function (ev) {
		var bookDetails = $(ev.currentTarget).serializeObject();
		var book = new Book();
		book.save(bookDetails, {
			success: function (book) {
				if(app.bookListView) app.bookListView.reload = true;
				if(app.bookReadView) app.bookReadView.reload = true;
				app.navigate('books', {trigger:true});
			},
			error: function () {
				if(resp.status == 200) {
					if(app.bookListView) app.bookListView.clearMessages = false;
					if(app.bookReadView) app.bookReadView.clearMessages = false;
					app.navigate('books', {trigger:true});
				} else {
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.saving}}"));
				}
			}
		});
		return false;
	},
	deleteBook: function (callback) {
		var self = this;
		this.book.destroy({
			success: function () {
				if(app.bookListView) app.bookListView.reload = true;
				app.navigate('books', {trigger:true});
				if(callback) callback();
			},
			error: function (req, resp) {
				if(resp.status == 200)
					app.pushMessageAndNavigate("message", "{{book.js.deleted}}".format(self.book.id), "books");
				else
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generis.js.deleting}}", self.book.id));
				if(callback) callback();
			}
		});
		return false;
	},
	render: function (options) {
		var self = this;
		if(options.id) {
			self.book = new Book({id: options.id});
			self.book.fetch({
				success: function (book) {
					$(self.el).empty();
					$(self.el).html(self.template({book: book}));
					return self;
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.loading}}", self.book.id));
				}
			});
		} else {
			$(self.el).empty();
			$(self.el).html(self.template({book: null}));
			return self;
		}
	}
});

window.BookReadView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	
	events: {
		'click .next': 'loadChapters'
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.book = new Book({id: options.id});
			self.book.fetch({
				success: function (book) {  
					$(self.el).empty();
					$(self.el).html(self.template({book: book}));
					self.loadChapters();
					return self;  
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thebook}}"));
				}
			});
		} else {
			app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.thebook}}"));
		}
	},
	loadChapters: function () {
		if(!this.chapterListView) {
			var self = this;
			viewLoader.load("ChapterListView", function() {
				self.chapterListView = new ChapterListView();
				self.chapterListView.render({book: self.book.get('id'), parentElement: $('#chapters')});
//				$('#chapters').html(self.chapterListView.el); //only for this time
			});
		} else {
			this.chapterListView.append({book: this.book.get('id')});
		}
	}
});