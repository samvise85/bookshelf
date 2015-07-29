window.AbstractModel = Backbone.Model.extend({
	parse: function(data) {
		this.error = data.error;
		this.errorMap = data.errorMap;
		return data.response ? data.response : data;
	}
});
window.AbstractCollection = Backbone.Collection.extend({
	parse: function(data) {
		this.error = data.error;
		this.errorMap = data.errorMap;
		return data.response ? data.response : data;
	}
});

window.User = window.AbstractModel.extend({
  urlRoot: '/users'  
});
window.Users = window.AbstractCollection.extend({
  url: '/users'
});

window.Language = window.AbstractModel.extend({
  urlRoot: '/languages'
});
window.Languages = window.AbstractCollection.extend({
  url : '/languages'
});

window.Route = window.AbstractModel.extend({
  urlRoot: '/analytics/routes'
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
	};
	this.get = function(key) {
		eval('var message = this.messages.' + key + ';');
		return message;
	};
	this.remove = function(key) {
		if(eval('this.messages.' + key + ';')) {
			eval('this.messages.' + key + ' = "";');
			this.refresh(key);
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
	this.clear = function() {
		for(key in message)
			this.remove(key);
	};
};