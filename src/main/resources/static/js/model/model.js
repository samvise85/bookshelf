window.Book = window.AbstractModel.extend({
	urlRoot : '/books'
});
window.Books = window.AbstractCollection.extend({
	url : '/books',
	model : Book
});

window.ChapterByPosition = window.AbstractModel.extend({
	url : null,
	initialize : function(options) {
		this.url = '/books/' + options.book + '/chapters';
	}
});
window.Chapter = window.AbstractModel.extend({
	urlRoot : null,
	initialize : function(options) {
		this.urlRoot = '/books/' + options.book + '/chapters';
	}
});
window.Chapters = window.AbstractCollection.extend({
	url : null,
	model : Chapter,
	initialize : function(options) {
		this.url = '/books/' + options.book + '/chapters';
	}
});

window.Comment = window.AbstractModel.extend({
	urlRoot : null,
	initialize : function(options) {
		this.urlRoot = '/streams/' + options.stream + '/comments';
	}
});
window.Comments = window.AbstractCollection.extend({
	url : null,
	model : Comment,
	initialize : function(options) {
		this.url = '/streams/' + options.stream + '/comments';
	}
});