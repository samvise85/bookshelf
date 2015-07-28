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
		this.renderView('AdminView', AdminView, 
				{rerender: true, selection: 'admin-menu'}, options);
    },
	
    analytics: function () {
    	var options = {};
		this.renderView('AnalyticsView', AnalyticsView, 
				{rerender: true, selection: 'admin-menu'}, options);
    },

    requests: function () {
		var options = {};
		this.renderView('RequestListView', RequestListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
    viewRequest: function (id) {
		var options = {id: id};
		this.renderView('RequestView', RequestView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	labels: function(id) {
		var options = {lang: id};
		this.renderView('LabelListView', LabelListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	languages: function() {
		var options = {};
		this.renderView('LanguageListView', LanguageListView, 
				{rerender: true, selection: 'admin-menu'}, options);
	},
	logout: function () {
		$.deleteCookie("bookshelf-username");
		$.deleteCookie("bookshelf-token");
		this.clear();
		window.location.replace(getAppPath() + "/#");
	}

});

app = new AdminRouter();
app.init();
