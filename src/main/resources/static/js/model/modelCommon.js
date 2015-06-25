window.User = Backbone.Model.extend({
  urlRoot: '/users'
});
window.Users = Backbone.Collection.extend({
  model: User,
  url: '/users',
  initialize: function(options) {
	  this.url = '/users' + (options.page ? '?page=' + options.page : '');
  }
});

window.Language = Backbone.Model.extend({
  urlRoot: '/languages',
});
window.Languages = Backbone.Collection.extend({
  url : '/languages',
  model: Language
});

window.Messages = function Messages() {
	this.messageCount = 0;
	this.messages = {};
	this.refreshView = false;
	this.initialize = function(refreshView) {
		this.refreshView = refreshView;
	};
	this.add = function(key, message) {
		eval('this.messages.' + key + ' = "' + message + '";');
		this.refresh(key);
		this.messageCount++;
	};
	this.get = function(key) {
		eval('var message = this.messages.' + key + ';');
		return message;
	};
	this.remove = function(key) {
		eval('this.messages.' + key + ' = "";');
		this.refresh(key);
		if(this.messageCount > 0) this.messageCount--;
	};
	this.isEmpty = function() {
		return this.messageCount === 0;
	};
	this.refresh = function(key) {
		if($('#'+key)) {
			$('#'+key).html(this.get(key));
		}
	};
};