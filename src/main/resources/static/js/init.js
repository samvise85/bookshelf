$(document).ready(function () {
	var date = new Date().getTime();
	var scripts = [["js/utils_" + date + ".js"], //loading utils.js it run an ajax prefilter so next requests need a /
	               [ "/js/model/modelCommon_" + date + ".js",
	                 "/js/model/model_" + date + ".js" ],
	               [ "/js/view/header_" + date + ".js",
	                 "/js/view/message_" + date + ".js",
	                 "/js/view/login_" + date + ".js",
	                 "/js/view/contact_" + date + ".js",
	                 "/js/view/user_" + date + ".js",
	                 "/js/view/book_" + date + ".js",
	                 "/js/view/chapter_" + date + ".js",
	                 "/js/view/comment_" + date + ".js" ],
	               [ "/js/main_" + date + ".js" ] ];
	
	var loader;
	var ScriptLoader = function ScriptLoader(scriptMatrix) {
		this.groupIndex = 0;
		this.scriptDownloaded = 0;
		this.scriptMatrix = scriptMatrix;

		this.getScripts = function () {
			var self = this;
			self.scriptMatrix[self.groupIndex].forEach(function (script) {
//				console.log("Loading " + script);
				$.getScript(script, function() {
//					console.log("Loaded " + script);
					self.addDownloaded();
				});
			});
		},
		this.addDownloaded = function() {
			var self = this;
			self.scriptDownloaded++;
//			console.log("Loaded group " + self.groupIndex + " file " + self.scriptDownloaded);
			if(self.scriptDownloaded >= self.scriptMatrix[self.groupIndex].length) {
				self.groupIndex++;
				self.scriptDownloaded = 0;
				if(self.groupIndex < self.scriptMatrix.length) {
//					console.log("Start group " + self.groupIndex);
					self.getScripts();
				} else {
					loader = null;
//					console.log("Loaded all groups, destroying loader.");
				}
			}
		}
		
		this.getScripts();
	};
	loader = new ScriptLoader(scripts);
});