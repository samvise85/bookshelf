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
        this.model.bind("change", this.render, this);
        this.model.bind("destroy", this.close, this);
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
			success: function(errors) {
				$('.request-'+ self.model.id + '-error').removeClass('loading');
				if(errors.models.length == 0) {
					$('.request-'+ self.model.id + '-error').removeClass('error');
					$('.request-'+ self.model.id + '-error').html('OK');
				} else {
					$('.request-'+ self.model.id + '-error').addClass('error');
					$('.request-'+ self.model.id + '-error').html('Error');
					self.error = errors.models[0];
				}
			},
			error: function(req, resp) {
				$('.request-'+ self.model.id + '-error').removeClass('loading');
				$('.request-'+ self.model.id + '-error').removeClass('error');
				$('.request-'+ self.model.id + '-error').html('OK');
			}
		});
	}
});

window.RequestListView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
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
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
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
				success: function(requests) {
					if(requests.models.length == 0) self.stopScroll = true;
					_.each(requests.models, function (request) {
						self.requests[request.id] = request;
						$('table tbody', self.el).append(new RequestListItemView(request).render().el);
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
				if(self.request) {
					$(self.el).empty();
					$(self.el).html(self.template({request: self.request}));
					self.loadError();
				}
			}
			if(!self.request) {
				self.request = new RestRequest(options);
				self.request.fetch({
					success: function(request) {
						$(self.el).empty();
						$(self.el).html(self.template({request: request}));
						self.loadError();
					},
					error: function (req, resp) {
						app.messageView.errors.push("This is not the request you're looking for.");
						app.rerenderMessages();
					}
				});
			}
		} else {
			app.messageView.errors.push("This is not the request you're looking for.");
			app.rerenderMessages();
		}
		return this;
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
				success: function(errors) {
					$('.request-'+ options.request + '-error').removeClass('loading');
					if(errors.models.length > 0) {
						self.error = errors.models[0];
						$(self.el).empty();
						$(self.el).html(self.template({error: self.error}));
					}
					if(options.callback)
						options.callback(self.el);
					return self;
				},
				error: function () {
				}
			});
		} else {
			app.messageView.errors.push("This is not the error you're looking for.");
			app.rerenderMessages();
		}
		return this;
	}
});