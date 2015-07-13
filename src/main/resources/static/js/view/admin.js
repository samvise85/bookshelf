
window.AdminView = Backbone.View.extend({
	reload : false,
	clearMessages : true,
	render: function () {
		$(self.el).empty();
		$(self.el).html(self.template());
	}
});