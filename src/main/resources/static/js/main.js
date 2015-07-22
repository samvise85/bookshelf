window.Router = Backbone.Router.extend({
	user: null,
	history: [],
	clearMessages: true,
	
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
    init: function () {
		var self = this;
		//load user
		if($.cookie("bookshelf-username")) {
			$.ajax({url: '/login',
				type:'GET',
				success:function (data) { self.onLoginSuccess(data); },
				error: function () { self.onLoginError("{{user.js.loginerr}}"); }
			});
		} else {
			self.initHistory();
			self.initView();
		}
	},
	onLoginSuccess: function(data) {
		data = eval(data);
		if(data.errors) {
			this.onLoginError("{{user.js.loginerr}}");
		} else if(data.response) {
			this.user = data.response;
			if(!this.user) {
				this.onLoginError("{{user.js.loginerr}} {{user.js.activateaccount}}");
			} else {
				this.initHistory();
				this.initView();
			}
		}
	},
	onLoginError: function(message) {
		if($.cookie("bookshelf-username")) {
			$.deleteCookie("bookshelf-username");
			$.deleteCookie("bookshelf-token");
		}
		this.initHistory();
		this.initView(null, null, message);
	},
	initHistory: function () {
		this.routesHit = 0;
        Backbone.history.start();
		Backbone.history.on('route', function(router, method) {
			this.routesHit++;
			app.history.push({
				method : method,
				fragment : Backbone.history.fragment
			});
//			console.log(app.history); //TODO create and save route
		}, this);
	},
	initView: function (message, warning, error) {
//		console.log("init");
		var self = this;
		viewLoader.load("HeaderView", function() {
			self.headerView = new HeaderView();
			$("#header").html(self.headerView.render().el);
		});
		viewLoader.load("MessageView", function() {
	        self.messageView = new MessageView();
	        if(message) self.messageView.messages.push(message);
	        if(warning) self.messageView.warnings.push(warning);
	        if(error) self.messageView.errors.push(error);
//	        console.log("init Rendering messages");
	        $('#messages').html(self.messageView.render().el);
		});
    },
	clear: function () {
		this.currentView = null;
		this.initView();
	},
	back: function() {
		if(this.routesHit > 1) {
			//more than one route hit -> user did not land to current page directly
			window.history.back();
		} else {
			//otherwise go to the home page. Use replaceState if available so
			//the navigation doesn't create an extra history entry
			this.navigate('/', {trigger:true, replace:true});
		}
	},
	getUser: function (callback) {
		return this.user;
	},
	isAdmin: function () {
		if(!$.cookie("bookshelf-username")) return false;
		return this.getUser() && this.getUser().admin === true;
	},
	renderView : function(viewName, View, headerOptions, viewOptions) {
		if(headerOptions)
			this.headerSelection = headerOptions.selection;
		
		//clear current view
		if(this.currentView) this.currentView.close();
		this.currentViewName = viewName;
		this.currentViewClass = View;
		
		//renders the view
		var self = this;
		var className = viewName.charAt(0).toUpperCase() + viewName.slice(1);
		viewLoader.load(className, function() {
			self.currentView = new self.currentViewClass();
			self.currentView.render(viewOptions);
			$("#content").html(self.currentView.el);
			self.currentView.delegateEvents();
		});
		
		//selects header
		if(this.headerView) {
			if(!headerOptions || headerOptions.rerender !== false)
				$("#header").html(this.headerView.render().el);
		}
		if(this.clearMessages && this.messageView) {
			this.messageView.clear();
			$('#messages').html(this.messageView.render().el);
		} else {
			this.clearMessages = true;
		}
	},
	rerenderView : function() {
		if(this.currentView) {
			var headerOptions = {rerender: true};
			this.renderView(this.currentViewName, this.currentViewClass, headerOptions, this.currentView.lastOptions);
		}
	},
	pushMessageAndNavigate: function(messageType, message, route) {
		if(this.messageView) {
			eval("this.messageView." + messageType + "s.push(message);");
			$('#messages').html(this.messageView.render().el);
		} 
		if(route != null) {
			this.clearMessages = false;
			this.navigate(route, {trigger:true});
		}
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

app = new Router();
app.init();
