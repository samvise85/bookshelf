
window.Book = Backbone.Model.extend({
  urlRoot: '/books',
    
  initialize:function () {
  	this.chapters = new Chapters({book:this.id});
  	this.chapters.url = '/books/' + this.id + '/chapters';
  }
});
window.Books = Backbone.Collection.extend({
  model: Book,
  url: '/books'
});

window.ChapterByPosition = Backbone.Model.extend({
  url: null,
  initialize: function(options) {
	this.url = '/books/' + options.book + '/chapters';// + (options.position ? '?position=' + options.position : '');
  }
});
window.Chapter = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/books/' + options.book + '/chapters';
  }
});
window.Chapters = Backbone.Collection.extend({
  url : null,
  model: Chapter,
  initialize: function(options) {
	  this.url = '/books/' + options.book + '/chapters';// + (options.page ? '?page=' + options.page : '');
  }
});

window.Comment = Backbone.Model.extend({
  urlRoot: null,
  initialize: function(options) {
	this.urlRoot = '/streams/' + options.stream + '/comments';
  }
});
window.Comments = Backbone.Collection.extend({
  url : null,
  model: Comment,
  initialize: function(options) {
	  this.url = '/streams/' + options.stream + '/comments' + (options.page ? '?page=' + options.page : '');
  }
});