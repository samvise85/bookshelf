window.AdminRouter = Backbone.Router.extend({
	user: null,
	savedViews: {},
	history: [],
	
    routes: {
		"": "analytics",
		"analytics": "analytics",
        "requests": "requests",
        "request/:id": "viewRequest",
        "languages": "languages",
        "labels/:id": "labels",
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
		var self = this;
		viewLoader.load("HeaderView", function() {
			self.headerView = new HeaderView();
			$("#header").html(self.headerView.render().el);
		});
		viewLoader.load("MessageView", function() {
	        self.messageView = new MessageView();
	        $('#messages').html(self.messageView.render().el);
		});
        
		Backbone.history.start();
		Backbone.history.on('route', function(router, method) {
			this.routesHit++;
			app.history.push({
				method : method,
				fragment : Backbone.history.fragment
			});
//			console.log(app.history);
		}, this);
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
		if(headerOptions && headerOptions.selection)
			this.headerSelection = headerOptions.selection;
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
			var self = this;
			var className = this.currentViewName.charAt(0).toUpperCase() + this.currentViewName.slice(1);
			viewLoader.load(className, function() {
				currentView = new self.currentViewClass();
				eval("self." + self.currentViewName + " = currentView;");
				currentView.render(viewOptions);
				$("#content").html(currentView.el);
			});
		} else {
			currentView.delegateEvents(); // delegate events when the view is recycled
			$("#content").html(currentView.el);
		}
		
		//selects header
		if(this.headerView) {
			if(headerOptions && headerOptions.rerender)
				$("#header").html(this.headerView.render().el);
			if(this.headerSelection)
				this.headerView.select(this.headerSelection);
		}
		//renders the messages
		if(this.messageView) {
			if(clearMessages)
				this.messageView.clear();
			$('#messages').html(this.messageView.render().el);
		}
	},
	
    admin: function () {
    	var options = {};
		this.renderView('adminView', AdminView, 
				{rerender: true, selection: 'admin-menu'}, options);
    },
	
    analytics: function () {
    	var options = {};
		this.renderView('analyticsView', AnalyticsView, 
				{rerender: true, selection: 'admin-menu'}, options);
    },

    requests: function () {
		var options = {};
		this.renderView('requestListView', RequestListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
    viewRequest: function (id) {
		var options = {id: id};
		this.renderView('requestView', RequestView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	labels: function(id) {
		var options = {lang: id};
		this.renderView('labelListView', LabelListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	languages: function() {
		var options = {};
		this.renderView('languageListView', LanguageListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	logout: function () {
		$.deleteCookie("bookshelf-username");
		$.deleteCookie("bookshelf-token");
		this.clear();
		window.location.replace(getAppPath() + "/#");
	},
	denied: function () {
		this.renderView('deniedView', DeniedView, null, null);
	}

});

app = new AdminRouter();
app.init();
