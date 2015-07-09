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
		
		$.ajax({url: '/login',
			type:'GET',
			success:function (data, textStatus, request) {
				app.clear();
				app.user = eval(data);
				if(!app.user) {
					$.deleteCookie("bookshelf-username");
					$.deleteCookie("bookshelf-token");
					app.pushMessageAndNavigate("error", "{{user.js.loginerr}} {{user.js.activateaccount}}"); 
				} else
					app.back();
			},
			error: function (request, textStatus, error) {
				$.deleteCookie("bookshelf-username");
				$.deleteCookie("bookshelf-token");
				app.pushMessageAndNavigate("error", "{{user.js.loginerr}}");
			}
		});
    }
});