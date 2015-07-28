window.ConfigRouter = window.BookshelfRouter.extend({
	
    routes: {
		"": "configure",
    },
    initHeader: function() {
		self.headerView = new HeaderView();
		$("#header").html(self.headerView.render({showMenu: false}).el);
    },
    
    configure: function () {
		this.renderView('ConfigureView', ConfigureView, {}, null);
	},
});

app = new ConfigRouter();
app.init();