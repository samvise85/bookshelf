window.AdminView = Backbone.View.extend({
	lastOptions : null,
	reload : true,
	clearMessages : true,
	newOptions: function(options) {
		return true;
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		$(self.el).empty();
		$(self.el).html(self.template());
	}
});

window.AnalyticsView = Backbone.View.extend({
	lastOptions : null,
	reload : true,
	clearMessages : true,
	newOptions: function(options) {
		return true;
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		$(self.el).empty();
		$(self.el).html(self.template());
	}
});

window.RequestListItemView = Backbone.View.extend({
    tagName:"tr",

    initialize:function (request) {
		this.model = request;
    },
    render:function () {
        $(this.el).html(this.template({request: this.model}));
        this.loadError();
        return this;
    },
	loadError: function() {
		var self = this;
		errors = new RestErrors();
		errors.fetch({
			data: $.param({"request": self.model.id}),
			success: function(errors) { self.onFetchSuccess(errors); },
			error: function(req, resp) { self.onFetchError(); }
		});
	},
	onFetchSuccess: function(errors) {
		if(errors.error) return this.onFetchError(errors.error);
		status = "ok";
		if(errors.models.length > 0) {
			this.error = errors.models[0];
			status = "error";
		}
		this.setErrorStatus(status);
	},
	onFetchError: function(message) {
		if(!message) message = "{{generic.js.error}}".format("{{generic.js.loading}}", "{{generic.js.theerror}}");
		this.setErrorStatus("undefined");
		app.pushMessageAndNavigate("error", message);
	},
	statusMap: { "ok": "{{request.list.ok}}", "error": "{{request.list.error}}", "undefined": "{{request.list.undefined}}"},
	setErrorStatus: function(status) {
		var self = this;
		$('#request-'+ this.model.id + '-error').removeClass(function() { return $('#request-'+ self.model.id + '-error').attr("class"); });
		$('#request-'+ this.model.id + '-error').addClass(status);
		$('#request-'+ this.model.id + '-error').html(this.statusMap[status]);
	}
});

window.RequestListView = Backbone.View.extend({
	lastOptions : null,
	clearMessages : true,
	page: 1,
	stopScroll: false,
	requests: {},

	initialize: function() {
		var self = this;
		viewLoader.load("RequestListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template({view: this}));
		this.append(options);
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
			var reqs = new RestRequests();
			reqs.fetch({
				data: $.param({"page": self.page}),
				success: function(requests) { self.onFetchSuccess(requests); },
				error: function () { self.onFetchError(); }
			});
			this.page++;
		}
	},
	onFetchSuccess: function(requests) {
		if(requests.error) return this.onFetchError(requests.error);
		if(requests.models.length == 0) this.stopScroll = true;
		_.each(requests.models, function (request) {
			this.requests[request.id] = request;
			$('table tbody', this.el).append(new RequestListItemView(request).render().el);
		}, this);
		return this;
	},
	onFetchError: function(message) {
		this.stopScroll = true;
		if(message) app.pushMessageAndNavigate("error", message);
	}
});

window.RequestView = Backbone.View.extend({
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
			if(app.requestListView && app.requestListView.requests) {
				self.request = app.requestListView.requests[options.id];
				if(self.request) self.onFetchSuccess(self.request);
			}
			if(!self.request) {
				self.request = new RestRequest(options);
				self.request.fetch({
					success: function(request) { self.onFetchSuccess(request); },
					error: function (req, resp) { self.onFetchError(); }
				});
			}
		} else {
			this.onFetchError();
		}
		return this;
	},
	onFetchSuccess: function(request) {
		if(request.error) return this.onFetchError(request.error);
		$(this.el).empty();
		$(this.el).html(this.template({request: request}));
		this.loadError();
	},
	onFetchError: function(message) {
		if(!message) message = "{{general.js.notfound}}".format("{{general.js.request}}");
		app.pushMessageAndNavigate("error", message);
	},
	loadError: function() {
		if(!this.errorView) {
			var self = this;
			viewLoader.load('ErrorView', function() {
				self.errorView = new ErrorView();
				self.errorView.render({
					request: self.request.id,
					parentViewName: 'requestView',
					callback: function(el) {
						$(".request-"+self.request.id+"-error").html(el);
					}
				});
			});
		}
	}
});

window.ErrorView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		var self = this;
		if(options.request) {
			errors = new RestErrors();
			errors.fetch({
				data: $.param({"request": options.request}),
				success: function(errors) { self.onFetchSuccess(errors); },
				error: function () { self.onFetchError(); }
			});
		} else {
			this.onFetchError();
		}
		return this;
	},
	onFetchSuccess: function(errors) {
		if(errors.error) return this.onFetchError(errors.error);
		$('.request-'+ this.lastOptions.request + '-error').removeClass('loading');
		if(errors.models.length > 0) {
			this.error = errors.models[0];
			$(this.el).empty();
			$(this.el).html(this.template({error: this.error}));
		}
		if(this.lastOptions.callback)
			this.lastOptions.callback(this.el);
	},
	onFetchError: function(message) {
		if(!message) message = "{{general.js.notfound}}".format("{{general.js.error}}");
		app.pushMessageAndNavigate("error", message);
	}
});