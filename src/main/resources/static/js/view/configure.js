window.ConfigureView = Backbone.View.extend({
	
	events: {
		"click #configure": "configure"
	},
    render:function (configured) {
        $(this.el).html(this.template({configured: configured}));
        return this;
    },

	configure: function() {
		var self = this;
		$.ajax({
			url: "/updateVersion",
			method: "PUT",
			success: function(response) { self.onConfigureSuccess(response); },
			error: function(re, resp) { self.onConfigureError(); },
		});
	},
	onConfigureSuccess: function(response) {
		if(response.error) this.onConfigureError(response.error);
		this.render(true);
		app.pushMessageAndNavigate("message", response.response);
	},
	onConfigureError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.configuring}}");
		app.pushMessageAndNavigate("error", message);
	}
});