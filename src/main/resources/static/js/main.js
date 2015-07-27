window.MainRouter = window.BookshelfRouter.extend({
	
    routes: {
		"": "books",
		"contact": "contact",
        "books": "books",
        "books?*querystring": "books",
        "books/new": "editBook",
		"book/:id/edit": "editBook",
        "book/:id": "viewBook",
//        "book/:book/chapters": "chapters",
        "book/:book/chapters/new": "editChapter",
        "book/:book/edit/:chapter": "editChapter",
        "book/:book/:position/:chapter": "viewChapter3",
        "book/:book/chapter?position=:position": "viewChapterByPosition",
		"users": "users",
		"user/:id/edit": "editUser",
		"user/:id/activate/:code": "activateUser",
		"user/:id": "viewUser",
		"register": "editUser",
		"forgot": "forgot",
		"reset/:code": "resetPassword",
        "login" : "login",
        "logout" : "logout"
    },
	languageChanged: function() {
		viewLoader.clear();
		this.clear();
//		alert('language changed!!!');
	},
	
	//route callbacks (or simply page rendering functions)
    home: function () {
    	var options = {};
		this.renderView('homeView', HomeView, 
				{selection: 'home-menu'}, options);
    },
    contact: function () {
    	var options = {};
		this.renderView('contactView', ContactView, 
				{selection: 'contact-menu'}, options);
    },
    books: function (querystring) {
    	options = null;
    	if(querystring) options = {queryparams: parseQueryString(querystring)}; 
		this.renderView('bookListView', BookListView, 
				{selection: 'book-menu'}, options);
	},
    editBook: function (id) {
		var options = {id: id};
		this.renderView('bookEditView', BookEditView, 
				{selection: 'book-menu'}, options);
	},
    viewBook: function (id) {
		var options = {id: id};
		this.renderView('bookReadView', BookReadView, 
				{selection: 'book-menu'}, options);
	},
    chapters: function (book) {
		var options = {book: book};
		this.renderView('chapterListView', ChapterListView, 
				{selection: 'book-menu'}, options);
	},
    editChapter: function (book, chapter) {
		var options = {book: book, id: chapter};
		this.renderView('chapterEditView', ChapterEditView,
				{selection: 'book-menu'}, options);
	},
    viewChapter: function (book, id) {
		var options = {book: book, id: id};
		this.renderView('chapterReadView', ChapterReadView, 
				{selection: 'book-menu'}, options);
	},
    viewChapter3: function (book, position, chapter) {
		var options = {book: book, title: decodeURI(chapter), position:position};
		this.renderView('chapterReadView', ChapterReadView, 
				{selection: 'book-menu'}, options);
	},
    viewChapterByPosition: function (book, position) {
		var options = {book: book, position: position};
		this.renderView('chapterReadView', ChapterReadView, 
				{selection: 'book-menu'}, options);
	},
	users: function () {
//		console.log("User list requested. Admin: " + this.isAdmin());
		if(this.isAdmin()) {
			var options = {};
			this.renderView('userListView', UserListView, 
					{selection: 'user-menu'}, options);
		} else {
			this.navigate('', {trigger:true});
		}
	},
    editUser: function (id) {
    	var self = this;
		if((!id && !self.getUser()) || (id && self.getUser() && (self.getUser().id == id || self.isAdmin()))) {
	    	var options = {id: id};
	    	selectedMenu = 'logged-menu';
	    	if(self.isAdmin()) selectedMenu = 'user-menu';
	    	self.renderView('userEditView', UserEditView, 
					{selection: selectedMenu}, options);
		} else {
			self.navigate('', {trigger:true});
		}
	},
	activateUser: function(id, code) {
		var self = this;
		if(!self.getUser()) {
	    	var options = {id: id, code: code};
	    	self.renderView('userActivateView', UserActivateView, 
					{selection: 'home-menu'}, options);
		} else {
			self.navigate('', {trigger:true});
		}
	},
    viewUser: function (id) {
    	var options = {id: id};
    	selectedMenu = 'logged-menu';
    	if(this.isAdmin() || (this.user && this.user.id != id)) selectedMenu = 'user-menu';
    	this.renderView('userView', UserView, 
				{selection: selectedMenu}, options);
	},
	forgot: function() {
		if(!this.getUser()) {
			this.renderView('forgotView', ForgotView, 
					{selection: null}, null);
		} else {
			this.navigate('', {trigger:true});
		}
	},
	resetPassword: function(code) {
		if(!this.getUser()) {
	    	var options = {code: code};
			this.renderView('resetView', ResetView, 
					{selection: null}, options);
		} else {
			this.navigate('', {trigger:true});
		}
	},
	
    login: function() {
    	var self = this;
		if(!self.getUser()) {
			self.renderView('loginView', LoginView, {selection: null}, null);
		} else {
			self.clear();
			self.navigate('', {trigger:true});
		}
    },
	logout: function () {
		$.deleteCookie("bookshelf-username");
		$.deleteCookie("bookshelf-token");
		this.clear();
		this.user = null;
		this.back();
	}

});

app = new MainRouter();
app.init();
