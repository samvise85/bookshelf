
window.RestRequest = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/analytics/requests';
  }
});
window.RestRequests = Backbone.Collection.extend({
  url : '/analytics/requests',
  model: RestRequest
});

window.RestError = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/analytics/errors';
  }
});
window.RestErrors = Backbone.Collection.extend({
  url : '/analytics/errors',
  model: RestError
});

window.Label = Backbone.Model.extend({
  urlRoot: '/labels',
});
window.Labels = Backbone.Collection.extend({
  url : '/labels',
  model: Label
});