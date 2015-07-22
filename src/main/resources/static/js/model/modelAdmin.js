
window.RestRequest = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/analytics/requests';
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});
window.RestRequests = Backbone.Collection.extend({
  url : '/analytics/requests',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});

window.RestError = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/analytics/errors';
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});
window.RestErrors = Backbone.Collection.extend({
  url : '/analytics/errors',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});

window.Label = Backbone.Model.extend({
  urlRoot: '/labels',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});
window.Labels = Backbone.Collection.extend({
  url : '/labels',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});