$(document).ready(function () {
	var date = new Date().getTime();
	var scripts = [["js/utils_" + date + ".js"],
	               [ "/js/model/modelCommon_" + date + ".js",
	                 "/js/model/modelAdmin_" + date + ".js" ],
	               [ "/js/view/header_" + date + ".js",
	                 "/js/view/message_" + date + ".js",
	                 "/js/view/login_" + date + ".js",
	                 "/js/view/analytics_" + date + ".js",
	                 "/js/view/label_" + date + ".js" ],
	               [ "/js/mainAdmin_" + date + ".js" ] ];
	
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
			if(loader.scriptDownloaded >= loader.scriptMatrix[loader.groupIndex].length) {
				loader.groupIndex++;
				loader.scriptDownloaded = 0;
				if(loader.groupIndex < loader.scriptMatrix.length) {
					loader.getScripts();
				} else {
					loader = null;
				}
			}
		}
		
		this.getScripts();
	};
	loader = new ScriptLoader(scripts);
});
