
window.Book = Backbone.Model.extend({
  urlRoot: '/books',
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  },
});
window.Books = Backbone.Collection.extend({
  url: '/books',
  model: Book,
  parse: function(data) {
	  this.error = data.error;
	  return data.response;
  }
});

window.ChapterByPosition = Backbone.Model.extend({
  url: null,
  initialize: function(options) {
	this.url = '/books/' + options.book + '/chapters';
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  }
});
window.Chapter = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/books/' + options.book + '/chapters';
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  }
});
window.Chapters = Backbone.Collection.extend({
  url : null,
  model: Chapter,
  initialize: function(options) {
	  this.url = '/books/' + options.book + '/chapters';// + (options.page ? '?page=' + options.page : '');
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  }
});

window.Comment = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/streams/' + options.stream + '/comments';
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  }
});
window.Comments = Backbone.Collection.extend({
  url : null,
  model: Comment,
  initialize: function(options) {
	  this.url = '/streams/' + options.stream + '/comments' + (options.page ? '?page=' + options.page : '');
  },
  parse: function(data) {
	  this.error = data.error;
	  return data.response ? data.response : data;
  }
});