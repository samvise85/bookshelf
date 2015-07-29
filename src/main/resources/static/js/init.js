
$.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
	if(options.url.indexOf('http') != 0)
		options.url = getAppPath() + options.url;
	options.headers = $.getToken();
});

window.getAppPath = function() {
	if(!window.appname) {
		var name = window.location.pathname.split("/")[1];
		window.appname = name.toLowerCase().indexOf("bookshelf") >= 0 ? name : null;
		
	}
	return '//' + window.location.host + (window.appname != null ? "/" + window.appname : "");
};

$.getToken = function () {
	if($.cookie('bookshelf-username') && $.cookie('bookshelf-token'))
		return {'bookshelf-username': $.cookie('bookshelf-username'), 'bookshelf-token': $.cookie('bookshelf-token')};
	return {};
}

window.ScriptLoader = function ScriptLoader(scriptMatrix) {
	this.groupIndex = 0;
	this.scriptDownloaded = 0;
	this.scriptMatrix = scriptMatrix;

	this.getScripts = function () {
		var self = this;
		self.scriptMatrix[self.groupIndex].forEach(function (script) {
			$.getScript(script, function() {
				self.addDownloaded(script);
			}).fail(function(){
				console.log(script + " has errors!");
			});
		});
	},
	this.addDownloaded = function(script) {
//		console.log("Downloaded " + script);
		var self = this;
		self.scriptDownloaded++;
		if(self.scriptDownloaded >= self.scriptMatrix[self.groupIndex].length) {
			self.groupIndex++;
			self.scriptDownloaded = 0;
			if(self.groupIndex < self.scriptMatrix.length)
				self.getScripts();
		}
	}
	
	this.getScripts();
};