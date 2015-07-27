window.AdminRouter = window.BookshelfRouter.extend({
	
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
