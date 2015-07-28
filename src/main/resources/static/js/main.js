window.MainRouter = window.BookshelfRouter.extend({
	
    routes: {
		"": "books",
		"contact": "contact",
        "books": "books",
        "books?*querystring": "books",
        "book/:id": "viewBook",
        "book/:book/:position/:chapter": "viewChapter",
        "book/:book/chapter?position=:position": "viewChapterByPosition",
        "publications": "publications",
        "publications/new": "editBook",
		"publication/:id/edit": "editBook",
        "publication/:book/chapters/new": "editChapter",
        "publication/:book/edit/:chapter": "editChapter",
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
	},
	
	//route callbacks (or simply page rendering functions)
    home: function () {
    	var options = {};
		this.renderView('HomeView', HomeView, 
				{selection: 'home-menu'}, options);
    },
    contact: function () {
    	var options = {};
		this.renderView('ContactView', ContactView, 
				{selection: 'contact-menu'}, options);
    },
    books: function (querystring) {
    	options = null;
    	if(querystring) options = {queryparams: parseQueryString(querystring)}; 
		this.renderView('BookListView', BookListView, 
				{selection: 'book-menu'}, options);
	},
    viewBook: function (id) {
		var options = {id: id};
		this.renderView('BookReadView', BookReadView, 
				{selection: 'book-menu'}, options);
	},
    viewChapter: function (book, position, chapter) {
		var options = {book: book, title: decodeURI(chapter), position:position};
		this.renderView('ChapterReadView', ChapterReadView, 
				{selection: 'book-menu'}, options);
	},
    viewChapterByPosition: function (book, position) {
		var options = {book: book, position: position};
		this.renderView('ChapterReadView', ChapterReadView, 
				{selection: 'book-menu'}, options);
	},
	publications: function (querystring) {
    	options = null;
    	params = parseQueryString(querystring)
    	params.author = this.user.id;
    	if(querystring) options = {queryparams: params}; 
		this.renderView('PublicationListView', PublicationListView, 
				{selection: 'publish-menu'}, options);
	},
    editBook: function (id) {
		var options = {id: id};
		this.renderView('BookEditView', BookEditView, 
				{selection: 'book-menu'}, options);
	},
    editChapter: function (book, chapter) {
		var options = {book: book, id: chapter};
		this.renderView('ChapterEditView', ChapterEditView,
				{selection: 'book-menu'}, options);
	},
	users: function () {
		var options = {};
		this.renderView('UserListView', UserListView, 
				{selection: 'user-menu'}, options);
	},
    editUser: function (id) {
    	var self = this;
		if((!id && !self.getUser()) || (id && self.getUser() && (self.getUser().id == id || self.isAdmin()))) {
	    	var options = {id: id};
	    	selectedMenu = 'logged-menu';
	    	if(self.isAdmin()) selectedMenu = 'user-menu';
	    	self.renderView('UserEditView', UserEditView, 
					{selection: selectedMenu}, options);
		} else {
			self.navigate('', {trigger:true});
		}
	},
	activateUser: function(id, code) {
		var self = this;
		if(!self.getUser()) {
	    	var options = {id: id, code: code};
	    	self.renderView('UserActivateView', UserActivateView, 
					{selection: 'home-menu'}, options);
		} else {
			self.navigate('', {trigger:true});
		}
	},
    viewUser: function (id) {
    	var options = {id: id};
    	selectedMenu = 'user-menu';
    	if(this.user && this.user.id == id)
    		selectedMenu = 'logged-menu';
    	this.renderView('UserView', UserView, 
				{selection: selectedMenu}, options);
	},
	forgot: function() {
		if(!this.getUser()) {
			this.renderView('ForgotView', ForgotView, 
					{selection: null}, null);
		} else {
			this.navigate('', {trigger:true});
		}
	},
	resetPassword: function(code) {
		if(!this.getUser()) {
	    	var options = {code: code};
			this.renderView('ResetView', ResetView, 
					{selection: null}, options);
		} else {
			this.navigate('', {trigger:true});
		}
	},
	
    login: function() {
    	var self = this;
		if(!self.getUser()) {
			self.renderView('LoginView', LoginView, {selection: null}, null);
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
