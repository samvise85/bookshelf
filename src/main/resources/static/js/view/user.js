window.UserListItemView = window.ListItemView.extend({});

window.UserListView = window.ListView.extend({
	itemClassName: "UserListItemView",
	ItemClass: UserListItemView,
	tableClass: ".users_table",
	ModelClass: Users
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
	events: {
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
					data: $.param({'username': username}),
					success: function (user) {
						if(!user.get('username') || user.error) return self.messages.remove("username_err");
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
				success: function(data) { self.onSaveSuccess(data); },
				error: function(req, resp) { self.onSaveError(); }
			});
		}
		return false;
	},
	onSaveSuccess: function(response) {
		if(response.error || response.errorMap) return this.onSaveError(response.error, response.errorMap);
		if(this.user && this.userj.language != this.user.get('language')) app.languageChanged();
		app.pushMessageAndNavigate("message", this.user ? "{{user.js.usermodified}}" : "{{user.js.usercreated}}", "users");
	},
	onSaveError: function(message, errorMap) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.saving}}", htmlEncode(this.userj.username))
		app.pushMessageAndNavigate("error", message);
		//errorMapToMessages(errorMap);//TODO
	},
	deleteUser: function (ev) {
		var self = this;
		this.user.destroy({
			success: function(response) { self.onDeleteSuccess(response); },
			error: function (req, resp) { self.onDeleteError(); }
		});
		return false;
	},
	onDeleteSuccess: function(response) {
		if(response.error) return this.onDeleteError(response.error);
		app.pushMessageAndNavigate("message", "{{user.js.userdeleted}}".format(this.user.id), "users");
	},
	onDeleteError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.deleting}}", htmlEncode(this.user.id));
		app.pushMessageAndNavigate("error", message); 
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			if(app.getUser() && (app.getUser().id == options.id || app.isAdmin())) {
				self.user = new User({id: options.id});
				self.user.fetch({
					success: function (user) { self.onFetchSuccess(user); },
					error: function () { self.onFetchError(); }
				});
			} else {
				this.onFetchError("{{user.js.cantedit}}");
			}
		} else {
			this.onFetchSuccess();
		}
	},
	onFetchSuccess: function(user) {
		if(user && user.error) return this.onFetchError(user.error);
		$(this.el).empty();
		$(this.el).html(this.template({user: user}));
		this.addLanguagesWhenReady();
		return this;
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.theuser}}");
		app.pushMessageAndNavigate("error", message);
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
			success: function(languages) { self.onLangFetchSuccess(languages); },
			error: function(req, resp) { self.onLangFetchError(); }
		});
	},
	onLangFetchSuccess: function(languages) {
		if(languages.error) return this.onLangFetchError(languages.error);
		_.each(languages.models, function(language) {
			var languageSelectItemView = new LanguageSelectItemView();
			languageSelectItemView.render({language: language, currLang: this.user ? this.user.get('language') : ''});
			$('select[name=language]').append(languageSelectItemView.el);
		});
	},
	onLangFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("{{generic.js.languagelist}}");
		app.pushMessageAndNavigate("error", message);
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
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.id) {
			self.user = new User({id: options.id});
			self.user.fetch({
				success: function (user) { self.onFetchSuccess(user); },
				error: function () { self.onFetchError(); }
			});
		} else {
			this.onFetchError();
		}
	},
	onFetchSuccess: function(user) {
		if(user.error) this.onFetchError(user.error);
		$(this.el).empty();
		$(this.el).html(this.template({user: user}));
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.notfound}}".format("user");
		app.pushMessageAndNavigate("error", message);
	}
});

window.UserActivateView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	render: function (options) {
		var self = this;
		if(options.id) {
			$.ajax({url: '/activationCode/' + options.code,
				type:'PUT',
				success:function (data) {
				},
				error: function (req, resp) { this.onActivationError(); }
			});
		} else {
			this.onActivationError();
		}
	},
	onActivationSuccess: function(data) {
		response = eval(data);
		if(!response.response || response.error) return this.onActivationError(response.error);
		app.pushMessageAndNavigate("message", "{{user.js.accountactivated}}".format(user.username), "");
	},
	onActivationError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{user.js.activating}}", "{{generic.js.theuser}}");
		app.pushMessageAndNavigate("error", message, "");
	}
});

window.ForgotView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	messages: new Messages(true),
	
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
				success:function (data) { self.onResetSuccess(data); },
				error: function (req, resp) { self.onResetError(); }
			});
		}
	},
	onResetSuccess: function(data) {
		response = eval(data);
		if(response.error || response.errorMap) return this.onResetError(response.error, response.errorMap);
		app.pushMessageAndNavigate("message", "{{user.js.forgotmailsent}}", "");
	},
	onResetError: function(message, errorMap) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.resetting}}", "{{generic.js.theuser}}");
		app.pushMessageAndNavigate("error", message);
		//errorMapToMessages(errorMap);//TODO
	}
});

window.ResetView = Backbone.View.extend({
	reload : true,
	clearMessages : true,
	messages: new Messages(true),
	
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
				success:function (data) { self.onResetSuccess(data); },
				error: function (req, resp) { self.onResetError(); }
			});
		}
	},
	onResetSuccess: function(data) {
		response = eval(data);
		if(!response.response || response.error || response.errorMap) return this.onResetError(response.error, response.errorMap);
		app.pushMessageAndNavigate("message", "{{user.js.resetok}}", "");
	},
	onResetError: function(message, errorMap) {
		if(!message) message = "{{user.js.resetko}}";
		app.pushMessageAndNavigate("error", message, "");
		//errorMapToMessages(errorMap);//TODO
	}
});