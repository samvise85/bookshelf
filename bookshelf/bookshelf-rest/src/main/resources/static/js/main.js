window.Router = Backbone.Router.extend({
	user: null,
	savedViews: {},
	
    routes: {
		"": "books",
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
		"users/new": "editUser",
		"user/:id": "viewUser",
        "login" : "login",
        "logout" : "logout",
		"denied" : "denied"
    },
	
    initialize: function () {
		this.routesHit = 0;
        this.headerView = new HeaderView();
        this.messageView = new MessageView();
		
		this.loadUser();
		Backbone.history.on('route', function() { this.routesHit++; }, this);
	},
	init: function () {
        $('.header').html(this.headerView.render().el);
        $('#messages').html(this.messageView.render().el);
    },
	clear: function () {
		_.each(this.savedViews, function (savedViewName) {
			eval('app.' + savedViewName + ' = null');
		});
		this.savedViews = {};
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
	loadUser: function () {
		var self = this;
		try {
			$.ajax({url: '/login',
				type:'POST',
				success:function (data, textStatus, request) {
					self.user = eval(data);
				},
				error: function (request, textStatus, error) {
					if($.cookie("bookshelf-username")) {
						$.removeCookie("bookshelf-username");
						$.removeCookie("bookshelf-token");
						self.messageView.errors.push("Username or password are not correct");
						self.messageView.rerender();
					}
				}
			});
		} catch(error) {
			console.log(error);
		}
	},
	getUser: function () {
		if(!$.cookie("bookshelf-username")) return null;
		var i = 0;
		while(!this.user && this.user != null && i < 10000) { i++; }
		return this.user;
	},
	isAdmin: function () {
		if(!$.cookie("bookshelf-username")) return false;
		return this.getUser().admin === true;
	},
	
	renderView : function(savedViewName, View, headerOptions, viewOptions) {
		this.savedViews[savedViewName] = savedViewName;
		
		var savedView = eval("this." + savedViewName);
		var clearMessages = !savedView || !(savedView.clearMessages === false); //if clear messages true or undefined
		
		//renders the view
		if (!savedView || savedView.reload || !savedView.newOptions || savedView.newOptions(viewOptions)) {
			savedView = new View();
			eval("this." + savedViewName + " = savedView;");
			savedView.render(viewOptions);
		} else {
			savedView.delegateEvents(); // delegate events when the view is recycled
		}
		$("#content").html(savedView.el);
		
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
		this.messageView.clear();
        if (!this.contactView) {
            this.contactView = new ContactView();
            this.contactView.render();
        }
        $('#content').html(this.contactView.el);
        this.headerView.select('contact-menu');
		this.messageView.rerender();
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
		this.messageView.clear();
		if (!this.userListView) {
            this.userListView = new UserListView();
            this.userListView.render();
        } else {
            this.userListView.delegateEvents(); // delegate events when the view is recycled
        }
        $("#content").html(this.userListView.el);
        this.headerView.select('user-menu');
	},
    editUser: function (id) {
		this.messageView.clear();
		if (!this.userEditView) {
            this.userEditView = new UserEditView();
            this.userEditView.render();
        } else {
            this.userEditView.delegateEvents(); // delegate events when the view is recycled
        }
        $("#content").html(this.userEditView.el);
        this.headerView.select('user-menu');
	},
    viewUser: function (id) {
		this.messageView.clear();
		if (!this.userView) {
            this.userView = new UserView();
            this.userView.render();
        } else {
            this.userView.delegateEvents(); // delegate events when the view is recycled
        }
        $("#content").html(this.userView.el);
        this.headerView.select('user-menu');
	},
	
    login: function() {
		this.renderView('loginView', LoginView, null, null);
    },
	logout: function () {
		$.removeCookie("bookshelf-username");
		$.removeCookie("bookshelf-token");
		this.clear();
		this.back();
	},
	denied: function () {
		this.renderView('deniedView', DeniedView, null, null);
	}

});


templateLoader.load(
	[
		"LoginView", "DeniedView",
		/*"HomeView", "ContactView", */"HeaderView", "MessageView",
		"BookListView", "BookEditView", "BookReadView", 
		"ChapterListView", "ChapterListItemView", "ChapterEditView", "ChapterReadView", 
		"UserListView", "UserEditView", "UserView"
	],
    function () {
        app = new Router();
		app.init();
        Backbone.history.start();
    }
);