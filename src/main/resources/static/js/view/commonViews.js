window.HeaderView = Backbone.View.extend({
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

window.AbstractView = Backbone.View.extend({
	lastOptions : null,
	messages: new Messages(true),
	
	onError: function(message, route) {
		if(!message) message = "{{generic.js.error}}".format("", "");
		app.pushMessageAndNavigate({"error": message}, route);
	},
	errorMapToMessages: function(errorMap) {
		for(errorKey in errorMap)
			this.messages.add(errorKey, errorMap[errorKey]);
	}
});

window.ListItemView = Backbone.View.extend({
    tagName:"tr",

    render: function(options) {
        $(this.el).html(this.template(options));
        return this;
    }
});

window.ListView = Backbone.View.extend({
	lastOptions : null,
	page: 1,
	num: null,
	stopScroll: false,
	maxh: 400,
	
	initialize: function() {
		var self = this;
		viewLoader.load(this.itemClassName, function() {
			self.listItemViewReady = true;
		});
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template({view: this}));
		this.calculateMaxH(options);
		
		$(this.el).find(this.tableClass).css("max-height", this.maxh + "px");
		this.num = parseInt(parseInt(this.maxh)/42) + 5;
		this.appendWhenReady(options);
	},
	calculateMaxH: function(options) {
		if(options.parentElement) {
			options.parentElement.html(this.el);
			pageHeight = $(window).height();
//			console.log("pageHeight: " + pageHeight);
//			console.log("offset: " + $(this.el).find('.chapters_table').offset().top);
			this.maxh = pageHeight- $(this.el).find(this.tableClass).offset().top;
		}
	},
	appendWhenReady: function (options) {
		if(this.listItemViewReady) {
			this.append(options);
		} else {
			var self  = this;
			setTimeout(function() { self.appendWhenReady(options); }, 100);
		}
	},
	append: function (options) {
		if(!(this.stopScroll === true)) {
			var self = this;
			var modelList = new this.ModelClass(this.getFetchOptions(options));
			modelList.fetch({
				data: $.param({"page": self.page, "num": self.num}),
				success: function(modelList) { self.onFetchSuccess(modelList); },
				error: function () { self.onFetchError(); }
			});
			this.page++;
		}
	},
	getFetchOptions: function(options) {
		return {}; //abstract
	},
	onFetchSuccess: function(modelList) {
		if(modelList.error) return this.onFetchError();
		if(modelList.models.length == 0 || modelList.models.length < this.num) this.stopScroll = true;
		var self = this;
		_.each(modelList.models, function (model) {
			$('table tbody').append(new self.ItemClass().render({model: model}).el);
		}, this);
		return this;
	},
	onFetchError: function() {
		this.stopScroll = true;
		$(this.el).empty();
		$(this.el).html(this.template());
	},
});