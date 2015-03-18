
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
	this.url = '/books/' + options.book + '/chapters' + (options.position ? '?position=' + options.position : '');
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
	  this.url = '/books/' + options.book + '/chapters' + (options.page ? '?page=' + options.page : '');
  }
});

window.User = Backbone.Model.extend({
  urlRoot: '/users'
});
window.Users = Backbone.Collection.extend({
  model: User,
  url: '/users'
});

window.Session = Backbone.Model.extend({
    token: null,
	username: null,

	initialize: function(username, token) {
		this.token = token;
		this.username = username;
	},
	authenticated: function() {
		return $.cookie('SPRING_SECURITY_REMEMBER_ME_COOKIE');
	},
	// Saves session information to cookie
	save: function(auth_hash) {
		$.cookie('SPRING_SECURITY_REMEMBER_ME_COOKIE', sessionid);
	},
	// Loads session information from cookie
	load: function(xhr) {
        alert(xhr.getResponseHeader("SPRING_SECURITY_REMEMBER_ME_COOKIE"));
		//this.token = $.cookie('SPRING_SECURITY_REMEMBER_ME_COOKIE');
	}
});