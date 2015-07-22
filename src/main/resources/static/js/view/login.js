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