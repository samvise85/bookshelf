window.LoginView = Backbone.View.extend({

    initialize:function () {
        console.log('Initializing Login View');
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
		
        console.log('Loggin in... ');
		var username = $('#username').val();
		var token = CryptoJS.SHA1(username + ":-1:" + $('#password').val() + ":bookshelf by Samvise85!");
		$.cookie("bookshelf-username", username);
		$.cookie("bookshelf-token", token.toString(CryptoJS.enc.Hex));
		
		$.ajax({url: '/login',
			type:'POST',
			success:function (data, textStatus, request) {
				app.clear();
				app.user = eval(data);
				app.back();
			},
			error: function (request, textStatus, error) {
				$.removeCookie("bookshelf-username");
				$.removeCookie("bookshelf-token");
				app.messageView.errors.push("Username or password are not correct");
				app.messageView.rerender();
			}
		});
    }
});