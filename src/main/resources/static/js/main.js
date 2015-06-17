window.Router = Backbone.Router.extend({
	user: null,
	savedViews: {},
	
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
						self.initView();
					},
					error: function (request, textStatus, error) {
						if($.cookie("bookshelf-username")) {
							$.deleteCookie("bookshelf-username");
							$.deleteCookie("bookshelf-token");
							self.initView();
							self.messageView.errors.push("Username or password are not correct");
							self.messageView.rerender();
						}
					}
				});
			} catch(error) {
				console.log(error);
			}
		} else {
			self.initView();
		}
	},
	initView: function () {
		this.routesHit = 0;
        this.headerView = new HeaderView();
        this.messageView = new MessageView();
        $('.header').html(this.headerView.render().el);
        $('#messages').html(this.messageView.render().el);
        
        Backbone.history.start();
		Backbone.history.on('route', function() { this.routesHit++; }, this);
    },
	clear: function () {
		_.each(this.savedViews, function (savedViewName) {
			eval('app.' + savedViewName + ' = null');
		});
		this.savedViews = {};
		this.currentView = null;
		this.user = null;
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
		this.savedViews[savedViewName] = savedViewName;
		this.currentViewName = savedViewName;
		this.currentViewClass = View;
		
		this.renderCurrentView(headerOptions, viewOptions);
	},
	rerenderView : function() {
		if(this.currentViewName && this.currentViewClass) {
			var currentView = eval("this." + this.currentViewName);
			
			var headerOptions = {rerender: true, selection: this.headerView.selection};
			currentView.reload = true;
			this.renderCurrentView(headerOptions, currentView.lastOptions);
		}
	},
	renderCurrentView : function(headerOptions, viewOptions) {
		var currentView = eval("this." + this.currentViewName);
		
		var clearMessages = !currentView || !(currentView.clearMessages === false); //if clear messages true or undefined
		
		//renders the view
		if (!currentView || currentView.reload || !currentView.newOptions || currentView.newOptions(viewOptions)) {
			currentView = new this.currentViewClass();
			eval("this." + this.currentViewName + " = currentView;");
			currentView.render(viewOptions);
		} else {
			currentView.delegateEvents(); // delegate events when the view is recycled
		}
		$("#content").html(currentView.el);
		
		//selects header
		if(headerOptions && headerOptions.rerender)
			$('.header').html(this.headerView.render().el);
		if(headerOptions && headerOptions.selection)
			this.headerView.select(headerOptions.selection);
		//renders the messages
		if(clearMessages)
			this.messageView.clear();
        $('#messages').html(this.messageView.render().el);
	},
	
    home: function () {
		this.renderView(function() {
				if (!app.homeView) {
					app.homeView = new HomeView();
					app.homeView.render();
				} else {
					app.homeView.delegateEvents(); // delegate events when the view is recycled
				}
				$("#content").html(app.homeView.el);
			},
			this.homeView ? this.homeView.clearMessages : true,
			'home-menu'
		);
    },

    contact: function () {
    	var options = {};
		this.renderView('contactView', ContactView, 
				{rerender: true, selection: 'contact-menu'}, options);
    },

    books: function () {
		this.renderView('bookListView', BookListView, 
				{rerender: true, selection: 'book-menu'}, null);
	},
    editBook: function (id) {
		var options = {id: id};
		this.renderView('bookEditView', BookEditView, 
				{rerender: true, selection: 'book-menu'}, options);
	},
    viewBook: function (id) {
		var options = {id: id};
		this.renderView('bookReadView', BookReadView, 
				{rerender: true, selection: 'book-menu'}, options);
	},
    chapters: function (book) {
		var options = {book: book};
		this.renderView('chapterListView', ChapterListView, 
				{rerender: true, selection: 'book-menu'}, options);
	},
    editChapter: function (book, id) {
		var options = {book: book, id: id};
		this.renderView('chapterEditView', ChapterEditView,
				{rerender: true, selection: 'book-menu'}, options);
	},
    viewChapter: function (book, id) {
		var options = {book: book, id: id};
		this.renderView('chapterReadView', ChapterReadView, 
				{rerender: true, selection: 'book-menu'}, options);
	},
    viewChapterByPosition: function (book, position) {
		var options = {book: book, position: position};
		this.renderView('chapterReadView', ChapterReadView, 
				{rerender: true, selection: 'book-menu'}, options);
	},
	users: function () {
		if(this.isAdmin()) {
			var options = {};
			this.renderView('userListView', UserListView, 
					{rerender: true, selection: 'user-menu'}, options);
		} else {
			this.navigate('', {trigger:true});
		}
	},
    editUser: function (id) {
    	var self = this;
//    	this.getUser(function () {
			if((!id && !self.getUser()) || (id && self.getUser() && (self.getUser().id == id || self.isAdmin()))) {
		    	var options = {id: id};
		    	self.renderView('userEditView', UserEditView, 
						{rerender: true, selection: 'user-menu'}, options);
			} else {
				self.navigate('', {trigger:true});
			}
//    	});
	},
    viewUser: function (id) {
    	var options = {id: id};
    	this.renderView('userView', UserView, 
				{rerender: true, selection: 'user-menu'}, options);
	},
	
    login: function() {
    	var self = this;
//    	this.getUser(function () {
			if(!self.getUser()) {
				self.renderView('loginView', LoginView, null, null);
			} else {
				self.navigate('', {trigger:true});
			}
//    	});
    },
	logout: function () {
		$.deleteCookie("bookshelf-username");
		$.deleteCookie("bookshelf-token");
		this.clear();
		this.back();
	},
	denied: function () {
		this.renderView('deniedView', DeniedView, null, null);
	}

});

window.viewList = [
	"LoginView", "DeniedView",
	/*"HomeView", */"ContactView", "HeaderView", "MessageView",
	"BookListView", "BookEditView", "BookReadView", 
	"ChapterListView", "ChapterListItemView", "ChapterEditView", "ChapterSelectItemView", "ChapterReadView",
	"CommentListView", "CommentListItemView", "CommentEditView",
	"UserListView", "UserListItemView", "UserEditView", "UserView"
];
templateLoader.load(
	viewList,
    function () {
        app = new Router();
        app.init();
    }
);