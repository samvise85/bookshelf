window.MessageView = Backbone.View.extend({
	
	messages : [],
	errors : [],
	warnings : [],

    render: function () {
		html = '';
		if(this.messages.length > 0) {
			html += this.template({type: 'success', messages:this.messages});
		}
		if(this.warnings.length > 0) {
			html += this.template({type: 'warning', messages:this.warnings});
		}
		if(this.errors.length > 0) {
			html += this.template({type: 'danger', messages:this.errors});
		}
		$(this.el).html(html);
		this.clear();
        return this;
    },
	rerender : function () {
		$('#messages').html(this.render().el);
	},
	
	clear : function () {
		this.messages = [];
		this.warnings = [];
		this.errors = [];
	}

});

window.DeniedView = Backbone.View.extend({
	events: {
		'click .close': 'close'
	},
	render: function() {
		$(this.el).html(this.template());
		return this;
	},
	close: function() {
		app.navigate('', {trigger:true});
	}
});