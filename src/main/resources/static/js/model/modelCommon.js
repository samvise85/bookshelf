window.User = Backbone.Model.extend({
  urlRoot: '/users',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});
window.Users = Backbone.Collection.extend({
  url: '/users',
  initialize: function(options) {
	  this.url = '/users' + (options.page ? '?page=' + options.page : '');
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});

window.Language = Backbone.Model.extend({
  urlRoot: '/languages',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});
window.Languages = Backbone.Collection.extend({
  url : '/languages',
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});

window.Messages = function Messages() {
	this.messageCount = function() {
		count = 0;
		for (key in this.messages)
			if(this.messages[key] != "") count++;
		return count;
	};
	this.messages = {};
	this.refreshView = false;
	this.initialize = function(refreshView) {
		this.refreshView = refreshView;
	};
	this.add = function(key, message) {
		eval('this.messages.' + key + ' = "' + message + '";');
		this.refresh(key);
//		this.messageCount++;
	};
	this.get = function(key) {
		eval('var message = this.messages.' + key + ';');
		return message;
	};
	this.remove = function(key) {
		if(eval('this.messages.' + key + ';')) {
			eval('this.messages.' + key + ' = "";');
			this.refresh(key);
//			if(this.messageCount > 0) this.messageCount--;
		}
	};
	this.isEmpty = function() {
		return this.messageCount() === 0;
	};
	this.refresh = function(key) {
		if($('#'+key)) {
			$('#'+key).html(this.get(key));
		}
	};
};