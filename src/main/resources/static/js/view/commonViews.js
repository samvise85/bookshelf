window.HeaderView = Backbone.View.extend({
	
    initialize: function () {
    },

    render: function (options) {
        $(this.el).html(this.template(options));
    	this.select(app.headerSelection);
    	return this;
    },

    select: function(menuItem) {
    	this.selection = menuItem;
        $('.nav li', this.el).removeClass('active');
        if(menuItem)
        	$('.' + menuItem, this.el).addClass('active');
    }
});

window.MessageView = Backbone.View.extend({
	
	messages : [],
	errors : [],
	warnings : [],

	render: function() {
		html = '';
		if(this.messages.length > 0)
			html += this.template({type: 'success', messages:this.messages});
		if(this.warnings.length > 0)
			html += this.template({type: 'warning', messages:this.warnings});
		if(this.errors.length > 0)
			html += this.template({type: 'danger', messages:this.errors});
		
		$(this.el).html(html);
		this.clear();
        return this;
    },
	clear : function() {
		this.messages = [];
		this.warnings = [];
		this.errors = [];
	}

});

window.LoginView = Backbone.View.extend({

    initialize:function () {
        //console.log('Initializing Login View');
    },

    events: {
        "click #loginButton": "login"
    },

    render:function () {
        $(this.el).html(this.template());
        return this;
    },

    login:function (event) {
        event.preventDefault(); // Don't let this button submit the form
        if(app.messageView)
        	app.messageView.clear();
		
		var username = $('#username').val();
		
		var token = createToken(username, $('#password').val());
		$.cookie("bookshelf-username", username);
		$.cookie("bookshelf-token", token);
		var self = this;
		
		$.ajax({url: '/login',
			type:'GET',
			success:function (data) { self.onSuccess(data); },
			error: function () { self.onError("{{user.js.loginerr}}"); }
		});
    },
    onSuccess: function(data) {
    	app.clear();
		data = eval(data);
		if(data.errors) {
			this.onError("{{user.js.loginerr}}");
		} else if(data.response) {
			app.user = data.response;
			if(!app.user)
				this.onError("{{user.js.loginerr}} {{user.js.activateaccount}}"); 
			else
				app.back();
		}
    },
    onError: function(message) {
		if($.cookie("bookshelf-username")) {
			$.deleteCookie("bookshelf-username");
			$.deleteCookie("bookshelf-token");
		}
		app.pushMessageAndNavigate("error", message);
    }
});