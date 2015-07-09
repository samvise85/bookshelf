window.Router = Backbone.Router.extend({
	user: null,
	savedViews: {},
	history: [],
	clearMessages: true,
	
    routes: {
		"": "books",
		"contact": "contact",
        "books": "books",
        "books/new": "editBook",
		"book/:id/edit": "editBook",
        "book/:id": "viewBook",
        "book/:book/chapters": "chapters",
        "book/:book/chapters/new": "editChapter",
        "book/:book/chapter/:id/edit": "editChapter",
        "book/:book/chapter/:id": "viewChapter",
        "book/:book/chapter?position=:position": "viewChapterByPosition",
		"users": "users",
		"user/:id/edit": "editUser",
		"user/:id/activate/:code": "activateUser",
		"user/:id": "viewUser",
		"register": "editUser",
        "login" : "login",
        "logout" : "logout",
		"denied" : "denied"
    },
    init: function () {
		var self = this;
		//load user
		if($.cookie("bookshelf-username")) {
			try {
				$.ajax({url: '/login',
					type:'GET',
					success:function (data, textStatus, request) {
						self.user = eval(data);
						self.initHistory();
						error = null;
						if(!self.user) {
							$.deleteCookie("bookshelf-username");
							$.deleteCookie("bookshelf-token");
							error = "{{user.js.loginerr}} {{user.js.activateaccount}}";
						}
						self.initView(null, null, error);
					},
					error: function (request, textStatus, error) {
						if($.cookie("bookshelf-username")) {
							$.deleteCookie("bookshelf-username");
							$.deleteCookie("bookshelf-token");
							self.initHistory();
							self.initView(null, null, "{{user.js.loginerr}}");
						}
					}
				});
			} catch(error) {
				console.log(error);
			}
		} else {
			self.initHistory();
			self.initView();
		}
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
		_.each(this.savedViews, function (savedViewName) {
			eval('app.' + savedViewName + ' = null');
		});
		this.savedViews = {};
		this.currentView = null;
		this.initView();
	},
	languageChanged: function() {
		viewLoader.clear();
		this.clear();
//		alert('language changed!!!');
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
	renderView : function(savedViewName, View, headerOptions, viewOptions) {
		if(headerOptions)
			this.headerSelection = headerOptions.selection;
		this.savedViews[savedViewName] = savedViewName;
		this.currentViewName = savedViewName;
		this.currentViewClass = View;
		
		this.renderCurrentView(headerOptions, viewOptions);
	},
	rerenderView : function() {
		if(this.currentViewName && this.currentViewClass) {
			var currentView = eval("this." + this.currentViewName);
			
			var headerOptions = {rerender: true};
			reload = currentView.reload;
			currentView.reload = true;
			this.renderCurrentView(headerOptions, currentView.lastOptions);
			currentView.reload = reload; //reset reload status to original value
		}
	},
	renderCurrentView : function(headerOptions, viewOptions) {
		eval("var currentView = this." + this.currentViewName + ";");
		
		//renders the view
		if (!currentView || currentView.reload || !currentView.newOptions || currentView.newOptions(viewOptions)) {
			var self = this;
			var className = this.currentViewName.charAt(0).toUpperCase() + this.currentViewName.slice(1);
			viewLoader.load(className, function() {
				if(!currentView) {
					currentView = new self.currentViewClass();
					eval("self." + self.currentViewName + " = currentView;");
				}
				currentView.render(viewOptions);
				$("#content").html(currentView.el);
			});
		} else {
			currentView.delegateEvents(); // delegate events when the view is recycled
			$("#content").html(currentView.el);
		}
		
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
    books: function () {
		this.renderView('bookListView', BookListView, 
				{selection: 'book-menu'}, null);
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
    editChapter: function (book, id) {
		var options = {book: book, id: id};
		this.renderView('chapterEditView', ChapterEditView,
				{selection: 'book-menu'}, options);
	},
    viewChapter: function (book, id) {
		var options = {book: book, id: id};
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
	},
	denied: function () {
		this.renderView('deniedView', DeniedView, null, null);
	}

});

app = new Router();
app.init();
