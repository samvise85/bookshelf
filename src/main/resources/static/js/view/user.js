window.UserListItemView = Backbone.View.extend({
	tagName:"tr",

	initialize:function (user) {
		this.model = user;
		this.model.bind("change", this.render, this);
		this.model.bind("destroy", this.close, this);
	},
	render:function () {
		$(this.el).html(this.template({user: this.model}));
		return this;
	}
});

window.UserListView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	page: 1,
	stopScroll: false,

	initialize: function() {
		var self = this;
		viewLoader.load("UserListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		if(app.isAdmin()) {
			this.lastOptions = options;
			$(this.el).empty();
			$(this.el).html(this.template({view: this}));
			this.append(options);
		} else {
			app.messageView.errors.push("You can't edit this profile!");
			app.messageView.rerender();
		}
	},
	append: function (options) {
		if(this.listItemViewReady) {
			this.appendWhenReady(options);
		} else {
			var self  = this;
			setTimeout(function() { self.append(options); }, 100);
		}
	},
	appendWhenReady: function (options) {
		if(!(this.stopScroll === true)) {
			var self = this;
			var users = new Users({page: this.page});
			users.fetch({
				success: function(users) {
					var self = this;
					if(users.models.length == 0) self.stopScroll = true;
					_.each(users.models, function (user) {
						$('table tbody', self.el).append(new UserListItemView(user).render().el);
					}, self);
					return self;
				},
				error: function () {
					$(self.el).empty();
					$(self.el).html(self.template());
					return self;
				}
			});
			this.page++;
		}
	}
});

window.UserEditView = Backbone.View.extend({
	lastOptions : null,
	reload : true, //sempre da ricaricare da zero
	clearMessages : true,
	messages: new Messages(true),

	initialize: function() {
		var self = this;
		viewLoader.load("LanguageSelectItemView", function() {
			self.listItemViewReady = true;
		});
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	events: {
//		'submit .edit-user-form': 'saveUser',
		'click .edit-user-form .save': 'saveUser',
		'click .delete': 'deleteUser',
		'blur #username': 'validateUsername',
		'blur #email': 'validateMail',
		'blur #check_email': 'validateCheckEmail',
		'blur #newpassword': 'validateNewPassword',
		'blur #check_password': 'validateCheckPassword'
	},
	validateUsername: function() {
		if(!$('#id') || $('#id').size() == 0) {
			var self = this;
			var username = $('#username').val();
			if(username == null || username.isEmpty()) {
				this.messages.add("username_err", "Select a username");
			} else {
				var userExists = new User({id: username});
				userExists.fetch({
					success: function (user) {
						self.messages.add("username_err", "Username is not available");
					},
					error: function () {
						self.messages.remove("username_err");
					}
				});
			}
		}
	},
	validateMail: function() {
		if(!$('#id') || $('#id').size() == 0) {
			var email = $('#email').val();
			if(email == null || email.isEmpty() || !validateEmail(email)) {
				this.messages.add("email_err", "Not a valid e-mail");
			} else {
				this.messages.remove("email_err");
			}
		}
	},
	validateCheckEmail: function() {
		if(!$('#id') || $('#id').size() == 0) {
			var email = $('#email').val();
			var check = $('#check_email').val();
			if(((email != null && !email.isEmpty()) && (check == null || check.isEmpty())) || check != email) {
				this.messages.add("check_email_err", "E-mail doesn't match");
			} else {
				this.messages.remove("check_email_err");
			}
		}
	},
	validateOldPassword: function() {
		var newpassword = $('#newpassword').val();
		if($('#id') && $('#id').size() > 0 && newpassword && !newpassword.isEmpty()) {
			var oldpassword = $('#oldpassword').val();
			var username = $('#username').val();
			var token = createToken(username, oldpassword);
			if(token != $.cookie("bookshelf-token")) {
				this.messages.add("oldpassword_err", "Wrong password!");
			} else {
				this.messages.remove("oldpassword_err");
			}
		}
	},
	validateNewPassword: function() {
		var newpassword = $('#newpassword').val();
		var a = newpassword == null || newpassword.isEmpty(); //no password
		var b = !$('#id') || $('#id').size() == 0; //no id
		var c = !validatePassword(newpassword); //non valid
		if(c && (b || !a)) {
			this.messages.add("newpassword_err", "Password must contain at least 8 character");
		} else {
			this.messages.remove("newpassword_err");
		}
	},
	validateCheckPassword: function() {
		var newpassword = $('#newpassword').val();
		var check = $('#check_password').val();
		if(((newpassword != null && !newpassword.isEmpty()) && (check == null || check.isEmpty())) || check != newpassword) {
			this.messages.add("check_password_err", "Password doesn't match");
		} else {
			this.messages.remove("check_password_err");
		}
	},
	validateAdmin: function() {
		
	},
	validate: function() {
		if(app.isAdmin()) {
			this.validateAdmin()
		}
		this.validateUsername();
		this.validateMail();
		this.validateCheckEmail();
		if(this.user)
			this.validateOldPassword();
		this.validateNewPassword();
		this.validateCheckPassword();
	},
	updateMessages: function(messages) {
		for (id in messages) {
		    var txt = messages[id];
		    $('#'+id).html(txt);
		}
	},
	saveUser: function (ev) {
		this.validate();
		if(this.messages.isEmpty()) {
			this.userj = {
				"id" : this.user ? this.user.id : null,
				"username" : $('#username').val(),
				"email" : $('#email').val(),
				"password" : CryptoJS.SHA1($('#newpassword').val()).toString(CryptoJS.enc.Hex),
				"firstname" : $('[name=firstname]').val(),
				"lastname" : $('[name=lastname]').val(),
				"admin" : app.isAdmin() ? $('.user_type .active').html() == 'Administrator' : null,
				"language" : $('[name=language]').val()
			};
			var self = this;
			$.ajax({url: '/login',
				type: this.user ? 'PUT' : 'POST',
				url: "/users" + (this.user ? "/" + this.user.id : ""),
			    data: JSON.stringify(self.userj),
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
				success:function (data, textStatus, request) {
					if(app.userListView) app.userListView.reload = true;
					if(!self.user && self.userj.language != self.user.get('language')) app.languageChanged();
					app.navigate('users', {trigger:true});
				},
				error: function (req, resp) {
					if(resp.status == 500) {
						app.messageView.errors.push("An error occurred saving " + htmlEncode(self.userj.username));
						app.messageView.rerender();
					} else if(resp.status != 403) {
						if(app.userListView) app.userListView.reload = true;
						app.navigate('users', {trigger:true});
					}
				}
			});
		}
		return false;
	},
	deleteUser: function (ev) {
		var self = this;
		this.user.destroy({
			success: function () {
				if(app.userListView) app.userListView.reload = true;
				router.navigate('users/', {trigger:true});
			},
			error: function (request, textStatus, error) {
				app.messageView.errors.push("An error occurred deleting " + htmlEncode(self.user.id));
				app.messageView.rerender();
			}
		});
		return false;
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			if(app.getUser() && (app.getUser().id == options.id || app.isAdmin())) {
				self.user = new User({id: options.id});
				self.user.fetch({
					success: function (user) {
						$(self.el).empty();
						$(self.el).html(self.template({user: user}));
						self.addLanguagesWhenReady();
						return self;
					},
					error: function () {
						app.messageView.errors.push("This is not the user you're looking for.");
						app.messageView.rerender();
					}
				});
			} else {
				app.messageView.errors.push("You can't edit this profile!");
				app.messageView.rerender();
			}
		} else {
			$(self.el).empty();
			$(self.el).html(self.template({user: null}));
			return self;
		}
	},
	recaptcha: function() {
		var self = this;
		grecaptcha.render('recaptcha', {
			'sitekey' : '6Le83QcTAAAAAIsK6kgz3-M73bODFXVqB8t-9Ul-'
		});
	},
	addLanguagesWhenReady: function() {
		if(this.listItemViewReady) {
			this.addLanguages();
		} else {
			var self  = this;
			setTimeout(function() { self.addLanguagesWhenReady(); }, 100);
		}
	},
	addLanguages: function() {
		var self  = this;
		var languages = new Languages();
		languages.fetch({
			success: function(languages) {
				_.each(languages.models, function(language) {
					var languageSelectItemView = new LanguageSelectItemView();
					languageSelectItemView.render({language: language, currLang: self.user ? self.user.get('language') : ''});
					$('select[name=language]').append(languageSelectItemView.el);
				})
			},
			error: function(req, resp) {
				app.messageView.errors.push("This is not the user you're looking for.");
				app.messageView.rerender();
			}
		});
	}
});

window.LanguageSelectItemView = Backbone.View.extend({
    render:function (options) {
        var html = this.template(options);
        this.setElement(html);
//        console.log(options);
//        console.log(this.el);
        return this;
    }
});

window.UserView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.user = new User({id: options.id});
			self.user.fetch({
				success: function (user) {
					$(self.el).empty();
					$(self.el).html(self.template({user: user}));
					return self;
				},
				error: function () {
					app.messageView.errors.push("This is not the user you're looking for.");
					app.messageView.rerender();
				}
			});
		} else {
			app.messageView.errors.push("This is not the user you're looking for.");
			app.messageView.rerender();
		}
	}
});