window.AdminRouter = Backbone.Router.extend({
	user: null,
	savedViews: {},
	history: [],
	
    routes: {
		"": "analytics",
		"analytics": "analytics",
        "requests": "requests",
        "request/:id": "viewRequest",
        "labels": "labels",
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
		Backbone.history.on('route', function(router, method) {
			this.routesHit++;
			app.history.push({
				method : method,
				fragment : Backbone.history.fragment
			});
			console.log(app.history);
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
	labels: function() {
		var options = {};
		this.renderView('labelListView', LabelListView, 
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

window.viewList = [
	"DeniedView", "HeaderView", "MessageView",
	/*"AdminView", */"AnalyticsView",
	"RequestListView", "RequestListItemView", "RequestView",
	"ErrorView", "LabelListView", "LabelListItemView"
];
templateLoader.load(
	viewList,
    function () {
        app = new AdminRouter();
        app.init();
    }
);