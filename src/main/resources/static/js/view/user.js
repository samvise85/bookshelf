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
			app.pushMessageAndNavigate("error", "{{user.js.cantedit}}");
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
		'blur #check_password': 'validateCheckPassword',
		'click .buttons-radio .btn': 'changeType'
	},
	validateUsername: function() {
		if(!$('#id') || $('#id').size() == 0) {
			var self = this;
			var username = $('#username').val();
			if(username == null || username.isEmpty()) {
				this.messages.add("username_err", "{{user.js.usernameerr}}");
			} else {
				var userExists = new User({id: username});
				userExists.fetch({
					success: function (user) {
						self.messages.add("username_err", "{{user.js.usernamenotavailable}}");
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
				this.messages.add("email_err", "{{user.js.emailerr}}");
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
				this.messages.add("check_email_err", "{{user.js.checkemailerr}}");
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
				this.messages.add("oldpassword_err", "{{user.js.oldpassworderr}}");
			} else {
				this.messages.remove("oldpassword_err");
			}
		}
	},
	validateNewPassword: function() {
		var newpassword = $('#newpassword').val();
		var a = newpassword != null && !newpassword.isEmpty(); //password valued
		var b = $('#id') && $('#id').size() == 1; //id
		var c = validatePassword(newpassword); //valid
		if(!((b || a) && c)) {
			this.messages.add("newpassword_err", "{{user.js.newpassworderr}}");
		} else {
			this.messages.remove("newpassword_err");
		}
	},
	validateCheckPassword: function() {
		var newpassword = $('#newpassword').val();
		var check = $('#check_password').val();
		if(((newpassword != null && !newpassword.isEmpty()) && (check == null || check.isEmpty())) || check != newpassword) {
			this.messages.add("check_password_err", "{{user.js.checkpassworderr}}");
		} else {
			this.messages.remove("check_password_err");
		}
	},
	validateAdmin: function() {
		
	},
	validateRecaptcha: function() {
		this.code = grecaptcha.getResponse();
		if(this.code) {
			this.messages.remove("captcha_err");
		} else {
			this.messages.add("captcha_err", "{{user.js.captchaerr}}");
		}
	},
	validate: function() {
		if(app.isAdmin())
			this.validateAdmin()
		this.validateUsername();
		this.validateMail();
		this.validateCheckEmail();
		if(this.user)
			this.validateOldPassword();
		this.validateNewPassword();
		this.validateCheckPassword();
		if(!app.getUser())
			this.validateRecaptcha();
	},
	changeType: function(ev) {
		$(ev.target.parentElement).find('.active').removeClass('active');
		$(ev.target).addClass('active');
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
				"admin" : app.isAdmin() ? $('.user_type .active').html() == '{{user.model.type.admin}}' : null,
				"language" : $('[name=language]').val()
			};
			var self = this;
			$.ajax({url: '/login',
				type: this.user ? 'PUT' : 'POST',
				url: "/users" + (this.user ? "/" + this.user.id : ""),
			    data: JSON.stringify(self.userj),
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
				success:function (data) {
					if(app.userListView) app.userListView.reload = true;
					if(self.user && self.userj.language != self.user.get('language')) app.languageChanged();
					
					app.pushMessageAndNavigate("message", self.user ? "{{user.js.usermodified}}" : "{{user.js.usercreated}}", "users");
				},
				error: function (req, resp) {
					if(resp.status == 500) {
						app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.saving}}", htmlEncode(self.userj.username)));
					} else if(resp.status != 403) {
						if(app.userListView) app.userListView.reload = true;
						app.pushMessageAndNavigate("message", self.user ? "{{user.js.usermodified}}" : "{{user.js.usercreated}}", "users");
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
				app.pushMessageAndNavigate("message", "{{user.js.userdeleted}}".format(self.user.id), "users");
			},
			error: function (req, resp, error) {
				if(resp.status == 500) {
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.deleting}}", htmlEncode(self.user.id))); 
				} else if(resp.status != 403) {
					if(app.userListView) app.userListView.reload = true;
					app.pushMessageAndNavigate("message", "{{user.js.userdeleted}}".format(self.user.id), "users");
				}
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
						app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("user"));
					}
				});
			} else {
				app.pushMessageAndNavigate("error", "{{user.js.cantedit}}");
			}
		} else {
			$(self.el).empty();
			$(self.el).html(self.template({user: null}));
			self.addLanguagesWhenReady();
			return self;
		}
	},
	recaptcha: function() {
		var self = this;
		grecaptcha.render('recaptcha', {
			'sitekey' : '6Le83QcTAAAAAIsK6kgz3-M73bODFXVqB8t-9Ul-'
//			'callback': function(code) {
//				alert("AAAA");
//				self.code = code;
//			}
		});
	},
	addLanguagesWhenReady: function() {
		if(this.listItemViewReady) {
//			console.log("Pronto");
			this.addLanguages();
		} else {
//			console.log("Non è pronto");
			var self  = this;
			setTimeout(function() { self.addLanguagesWhenReady(); }, 100);
		}
	},
	addLanguages: function() {
		var self  = this;
		var languages = new Languages();
		languages.fetch({
			success: function(languages) {
//				console.log("Trovate "  + languages.length + " lingue");
				_.each(languages.models, function(language) {
					var languageSelectItemView = new LanguageSelectItemView();
					languageSelectItemView.render({language: language, currLang: self.user ? self.user.get('language') : ''});
					$('select[name=language]').append(languageSelectItemView.el);
				})
			},
			error: function(req, resp) {
				app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("user"));
			}
		});
	}
});

window.LanguageSelectItemView = Backbone.View.extend({
    render:function (options) {
        var html = this.template(options);
        this.setElement(html);
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
				},
				error: function () {
					app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("user"));
				}
			});
		} else {
			app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("user"));
		}
	}
});

window.UserActivateView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	newOptions: function(options) {
		return true;
	},
	render: function (options) {
		var self = this;
		if(options.id) {
			$.ajax({url: '/activationCode/' + options.code,
				type:'PUT',
				success:function (data, textStatus, request) {
					user = eval(data);
					if(!user)
						app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{user.js.activating}}", "{{generic.js.theuser}}"), "");
					else
						app.pushMessageAndNavigate("message", "{{user.js.accountactivated}}".format(user.username), "");
				},
				error: function (request, textStatus, error) {
					app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{user.js.activating}}", "{{generic.js.theuser}}"), "");
				}
			});
		} else {
			app.pushMessageAndNavigate("error", "{{generic.js.notfound}}".format("{{generic.js.theuser}}"), "");
		}
	}
});

window.ForgotView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	messages: new Messages(true),
	newOptions: function(options) {
		return true;
	},
	
	events: {
		'click .edit-user-form .save': 'sendMail',
		'blur #username': 'validateUsernameOrMail'
	},
	validateUsernameOrMail: function() {
		this.messages.remove("username_err");
		var username = $('#username').val();
		if(username == null || username.isEmpty())
			this.messages.add("username_err", "{{user.js.usernameormailerr}}");
	},
	validateRecaptcha: function() {
		this.messages.remove("captcha_err");
		this.code = grecaptcha.getResponse();
		if(!this.code)
			this.messages.add("captcha_err", "{{user.js.captchaerr}}");
	},
	validate: function() {
		this.validateUsernameOrMail();
		this.validateRecaptcha();
	},
	render: function () {
		$(this.el).empty();
		$(this.el).html(this.template());
		return this;
	},
	sendMail: function() {
		this.validate();
		if(this.messages.isEmpty()) {
			this.userj = {
				"username" : $('#username').val()
			};
			var self = this;
			$.ajax({
				type: 'PUT',
				url: '/forgot',
			    data: JSON.stringify(self.userj),
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
				success:function (data) {
					app.pushMessageAndNavigate("message", "{{user.js.forgotmailsent}}", "");
				},
				error: function (req, resp) {
					if(resp.status == 500) {
						app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.resetting}}", "{{generic.js.theuser}}"));
					} else if(resp.status == 200) {
						app.pushMessageAndNavigate("message", "{{user.js.forgotmailsent}}", "");
					}
				}
			});
		}
	}
});

window.ResetView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	messages: new Messages(true),
	newOptions: function(options) {
		return true;
	},
	
	events: {
		'click .edit-user-form .save': 'sendMail',
		'blur #newpassword': 'validateNewPassword',
		'blur #check_password': 'validateCheckPassword'
	},
	validateNewPassword: function() {
		this.messages.remove("newpassword_err");
		
		var newpassword = $('#newpassword').val();
		var a = newpassword != null && !newpassword.isEmpty(); //password valued
		var c = validatePassword(newpassword); //valid
		if(!(a && c))
			this.messages.add("newpassword_err", "{{user.js.newpassworderr}}");
	},
	validateCheckPassword: function() {
		this.messages.remove("check_password_err");
		
		var newpassword = $('#newpassword').val();
		var check = $('#check_password').val();
		if(((newpassword != null && !newpassword.isEmpty()) && (check == null || check.isEmpty())) || check != newpassword)
			this.messages.add("check_password_err", "{{user.js.checkpassworderr}}");
	},
	validate: function() {
		this.validateNewPassword();
		this.validateCheckPassword();
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template());
		return this;
	},
	sendMail: function() {
		this.validate();
		if(this.messages.isEmpty()) {
			this.userj = {
				"password" : CryptoJS.SHA1($('#newpassword').val()).toString(CryptoJS.enc.Hex)
			};
			var self = this;
			$.ajax({
				type: 'PUT',
				url: '/resetCode/' + this.lastOptions.code,
			    data: JSON.stringify(self.userj),
			    contentType: "application/json; charset=utf-8",
			    dataType: "json",
				success:function (data) {
					if(data == null)
						app.pushMessageAndNavigate("error", "{{user.js.resetko}}", "");
					else
						app.pushMessageAndNavigate("message", "{{user.js.resetok}}", "");
				},
				error: function (req, resp) {
					if(resp.status == 500) {
						app.pushMessageAndNavigate("error", "{{generic.js.error}}".format("{{generic.js.resetting}}", "{{generic.js.theuser}}"));
					} else if(resp.status == 200) {
						if(data == null)
							app.pushMessageAndNavigate("error", "{{user.js.resetko}}", "");
						else
							app.pushMessageAndNavigate("message", "{{user.js.resetok}}", "");
					}
				}
			});
		}
	}
});