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
		app.messageView.clear();
		
        //console.log('Loggin in... ');
		var username = $('#username').val();
		
		var token = createToken(username, $('#password').val());
		$.cookie("bookshelf-username", username);
		$.cookie("bookshelf-token", token);
		
		$.ajax({url: '/login',
			type:'GET',
			success:function (data, textStatus, request) {
				app.clear();
				app.user = eval(data);
				app.back();
			},
			error: function (request, textStatus, error) {
				$.deleteCookie("bookshelf-username");
				$.deleteCookie("bookshelf-token");
				app.messageView.errors.push("Username or password are not correct");
				app.messageView.rerender();
			}
		});
    }
});