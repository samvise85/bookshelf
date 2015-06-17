

window.BookListView = Backbone.View.extend({
	reload : false,
	clearMessages : true,
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
				app.messageView.errors.push("An error occurred loading book list");
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
		'click .delete': 'deleteBook'
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
					app.messageView.errors.push("An error occurred " + (book.id ? "updating " + book.id : "saving"));
					if(app.bookListView) app.bookListView.clearMessages = false;
					if(app.bookReadView) app.bookReadView.clearMessages = false;
					app.navigate('books', {trigger:true});
				}
			}
		});
		return false;
	},
	deleteBook: function (ev) {
		var self = this;
		this.book.destroy({
			success: function () {
				if(app.bookListView) app.bookListView.reload = true;
				app.navigate('books', {trigger:true});
			},
			error: function (req, resp) {
				if(resp.status == 200) {
					app.navigate('books', {trigger:true});
				} else {
					app.messageView.errors.push("An error occurred deleting of " + self.book.id);
					app.messageView.rerender();
					$('button.cancel').click();
				}
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
					app.messageView.errors.push("An error occurred loading " + self.book.id);
					app.messageView.rerender();
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
		'click .next': 'loadChapters',
		'scroll .chapters-table': 'scroll'
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
					app.messageView.errors.push("This is not the book you're looking for");
					app.messageView.rerender();
				}
			});
		} else {
			app.messageView.errors.push("This is not the book you're looking for");
			app.messageView.rerender();
		}
	},
	scroll: function (e) {
		if ($('.chapters-table').offset().top + $('.chapters-table').scrollTop() >= $('.chapters-table')[0].scrollHeight) {
			this.loadChapters();
		}
	},
	loadChapters: function () {
		if(!this.chapterListView) {
			this.chapterListView = new ChapterListView();
			this.chapterListView.render({book: this.book.get('id')});
			$('#chapters').html(this.chapterListView.el);
		} else {
			this.chapterListView.append({book: this.book.get('id')});
		}
	}
});