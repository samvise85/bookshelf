$(document).ready(function () {
	var date = new Date().getTime();
	var scripts = [["js/utils_" + date + ".js"],
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
				$.getScript(script, self.addDownloaded);
			});
		},
		this.addDownloaded = function() {
			loader.scriptDownloaded++;
			//console.log("Loaded group " + loader.groupIndex + " file " + loader.scriptDownloaded);
			if(loader.scriptDownloaded >= loader.scriptMatrix[loader.groupIndex].length) {
				loader.groupIndex++;
				loader.scriptDownloaded = 0;
				if(loader.groupIndex < loader.scriptMatrix.length) {
					//console.log("Start group " + loader.groupIndex);
					loader.getScripts();
				} else {
					loader = null;
					//console.log("Loaded all groups, destroying loader.");
				}
			}
		}
		
		this.getScripts();
	};
	loader = new ScriptLoader(scripts);
});
