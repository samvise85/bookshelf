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

	//route callbacks (or simply page rendering functions)
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
