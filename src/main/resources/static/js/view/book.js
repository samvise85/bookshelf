
window.BookListView = Backbone.View.extend({
	reload : false,
	clearMessages : false,
	render: function (options) {
		var self = this;
		var books = new Books();
		books.fetch({
			data: options && options.queryparams ? $.param(options.queryparams) : null,
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
	reload : true,
	clearMessages : true,
	messages: new Messages(true),
	
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
		'submit .edit-book-form': 'saveBook',
		'blur [name=title]': 'validateTitle',
		'blur [name=year]': 'validateYear',
	},
	validateTitle: function() {
		this.messages.remove("title_err");
		var title = $('[name=title]').val();
		if(title == null || title.isEmpty())
			this.messages.add("title_err", "{{book.js.titleerr}}");
	},
	validateYear: function() {
		$("#year_err").removeClass("warn");
		$('#year_err').addClass("error");
		$("#year_err").empty();
		this.messages.remove("year_err");
		
		var year = $('[name=year]').val();
		if(year != null && !year.isEmpty()) {
			var y = parseInt(year);
			console.log(y > new Date().getFullYear());
			if(isNaN(y))
				this.messages.add("year_err", "{{book.js.yearnan}}");
			else if(y > new Date().getFullYear()) {
				$("#year_err").html("{{book.js.yearfuture}}");
				$("#year_err").removeClass("error");
				$('#year_err').addClass("warn");
			}
		}
	},
	validate: function() {
		this.validateTitle();
		this.validateYear();
	},
	saveBook: function (ev) {
		this.validate();
		if(this.messages.isEmpty()) {
			var bookDetails = $(ev.currentTarget).serializeObject();
			var book = new Book();
			var self = this;
			book.save(bookDetails, {
				success: function (book) { self.onSaveSuccess(book); },
				error: function (req, resp) { self.onSaveError(); }
			});
		}
		return false;
	},
	onSaveSuccess: function(book) {
		if(book.error || book.errorMap) return this.onSaveError(book.error, book.errorMap);
		app.navigate('books', {trigger:true});
	},
	onSaveError: function(message, errorMap) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.saving}}");
		app.pushMessageAndNavigate("error", message);
		//errorMapToMessages(errorMap);//TODO
	},
	deleteBook: function (callback) {
		var self = this;
		this.book.destroy({
			success: function (response ) { self.onDeleteSuccess(response, callback); },
			error: function (req, resp) { self.onDeleteError(null, callback); }
		});
		return false;
	},
	onDeleteSuccess: function(response, callback) {
		if(response.error) return this.onDeleteError(response.error, callback);
		app.pushMessageAndNavigate("message", "{{book.js.deleted}}".format(htmlEncode(this.book.get('title'))), "books");
		if(callback) callback();
	},
	onDeleteError: function(message, callback) {
		if(!message) message = "{{generic.js.error}}".format("{{generis.js.deleting}}", htmlEncode(this.book.get('title')));
		app.pushMessageAndNavigate("error", message);
		if(callback) callback();
	},
	render: function (options) {
		var self = this;
		if(options.id) {
			self.book = new Book({id: options.id});
			self.book.fetch({
				success: function (book) { self.onFetchSuccess(book); },
				error: function () { self.onFetchError(); }
			});
		} else {
			return self.onFetchSuccess(null);
		}
	},
	onFetchSuccess: function(book) {
		if(book && book.error) return this.onFetchError(book.error);
		$(this.el).empty();
		$(this.el).html(this.template({book: book}));
		return this;
	}, 
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.loading}}", this.book.id);
		app.pushMessageAndNavigate("error", message);
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
				success: function (book) { self.onFetchSuccess(book); },
				error: function () { self.onFetchError(); }
			});
		} else {
			self.onFetchError();
		}
	},
	onFetchSuccess: function(book) {
		if(book.error) return this.onFetchError(book.error);
		$(this.el).empty();
		$(this.el).html(this.template({book: book}));
		this.loadChapters();
		return this;
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.thebook}}");
		app.pushMessageAndNavigate("error", message);
	},
	loadChapters: function () {
		if(!this.chapterListView) {
			var self = this;
			viewLoader.load("ChapterListView", function() {
				self.chapterListView = new ChapterListView();
				self.chapterListView.render({book: self.book.get('id'), parentElement: $('#chapters')});
//				$('#chapters').html(self.chapterListView.el); //only for this time I give the control to the subview
			});
		} else {
			this.chapterListView.append({book: this.book.get('id')});
		}
	}
});