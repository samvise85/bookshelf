
window.createToken = function (username, password) {
//	return CryptoJS.SHA1(username + ":-1:" + password + ":bookshelf by Samvise85!").toString(CryptoJS.enc.Hex);
	return CryptoJS.SHA1(username + ":-1:" + CryptoJS.SHA1(password).toString(CryptoJS.enc.Hex) + ":bookshelf by Samvise85!").toString(CryptoJS.enc.Hex);
}

$.ajaxSetup({
    statusCode: {
        401: function(){
			window.oldLocation = window.location.href;
            // Redirect the to the login page.
			if(app && !app.getUser()) {
				window.location.replace(getAppPath() + '#login');
			}
        }
    }
});

window.htmlEncode = function(value){
  return $('<div/>').text(value).html();
}
$.fn.serializeObject = function() {
  var o = {};
  var a = this.serializeArray();
  $.each(a, function() {
	  if (o[this.name] !== undefined) {
		  if (!o[this.name].push) {
			  o[this.name] = [o[this.name]];
		  }
		  o[this.name].push(this.value || '');
	  } else {
		  o[this.name] = this.value || '';
	  }
  });
  return o;
};

$.urlParam = function(name){
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results==null){
       return null;
    }
    else{
       return results[1] || 0;
    }
}

window.viewLoader = {
	loaded: {},
	load: function(viewName, callback) {
		if(!this.loaded[viewName]) {
			var self = this;
//			console.log("Load view " + viewName);
			templateLoader.load([viewName], function() {
				self.loaded[viewName] = viewName;
				callback();
			});
		} else {
			callback();
		}
	},
	clear: function() {
//		console.log("Cleared views");
		this.loaded = {};
	}
};

window.templateLoader = {
	load: function(views, callback) {
		var deferreds = [];
		var date = new Date().getTime();
		
		$.each(views, function(index, view) {
			if (window[view]) {
				var resource = getAppPath() + '/tpl/' + view + "_" + date +'.html';
				deferreds.push($.get(resource, function(data) {
					try {
//						console.log("Caricato template in vista " + view);
						window[view].prototype.template = _.template(data);
					} catch(e) {
//						console.error("Error loading template of view " + view);
						throw e;
					}
				}, 'html'));
			} else {
				alert(view + " not found");
			}
		});

		$.when.apply(null, deferreds).done(callback);
	}
};

window.arraysEqual = function (a1,a2) {
    return JSON.stringify(a1)==JSON.stringify(a2);
}


if (!String.prototype.isEmpty) {
	String.prototype.isEmpty = function() {
	    return (this.length === 0 || !this.trim());
	};
}

if (!String.prototype.format) {
	String.prototype.format = function() {
		var args = arguments;
		return this.replace(/{(\d+)}/g, function(match, number) { 
			return typeof args[number] != 'undefined' ? args[number] : match;
		});
	};
}

window.validateEmail = function (str) {
	var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    return (!str || re.test(str));
}

window.validatePassword = function (str) {
	var re = /^.{8,}$/;
    return (!str || re.test(str));
}

window.compareDate = function (remote, recent) {
	if(!recent) recent = new Date();
	var diff = recent.getTime() - remote.getTime();
	
	if(diff < 60000) {
		return "Just a moment ago";
	} else if(diff < 120000) {
		return "A minute ago";
	} else if(diff < 3600000) {
		return new Date(diff).getMinutes() + " minutes ago";
	} else if(diff < 86400000) {
		return new Date(diff).getHours() + " hours ago"; //FIXME prende le ore con il fuso orario (invece di 1 mette 2)
	} else if(new Date(diff).getMonth() < 2) {
		return new Date(diff).getDay() + " days ago";
	} else {
		return "On " + remote.toLocaleDateString();
	}
}

$.deleteCookie = function(name) {
	  document.cookie = name + '=; expires=Thu, 01 Jan 1980 00:00:01 GMT;';
}

window.onloadCallback = function() {
	grecaptcha.render('recaptcha', {
		'sitekey' : '6Le83QcTAAAAAIsK6kgz3-M73bODFXVqB8t-9Ul-',
		'data-callback'  : function(response) {
			
		}
	});
}

window.nl2br = function(string) {
	return string.replace(/\n/g, '<br/>').replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;');
}

window.parseQueryString = function(queryString) {
	var params = {};
    if(queryString){
        _.each(
            _.map(decodeURI(queryString).split(/&/g),function(el,i){
                var aux = el.split('='), o = {};
                if(aux.length >= 1){
                    var val = undefined;
                    if(aux.length == 2)
                        val = aux[1];
                    o[aux[0]] = val;
                }
                return o;
            }),
            function(o){
                _.extend(params,o);
            }
        );
    }
    return params;
}

Backbone.View.prototype.close = function(){
	//important!!! This should be always called when changing view
	//but actually i don't know which one of these is really necessary
	this.undelegateEvents();
	this.remove();
	this.unbind();
	this.off();
	if (this.onClose)
		this.onClose(); //defie this function to unbind specific model bindings
}

window.downloadFile = function(content, type, name) {
    var file = new Blob([content], {type: type});
	var a = document.createElement("a");
    a.href = URL.createObjectURL(file);
    a.download = name;
    a.click();
}

window.BookshelfRouter = Backbone.Router.extend({
	user: null,
	history: [],
	clearMessages: true,

    init: function () {
		var self = this;
		//load user
		if($.cookie("bookshelf-username")) {
			$.ajax({url: '/login',
				type:'GET',
				success:function (data) { self.onLoginSuccess(data); },
				error: function () { self.onLoginError("{{user.js.loginerr}}"); }
			});
		} else {
			self.initHistory();
			self.initView();
		}
	},
	onLoginSuccess: function(data) {
		data = eval(data);
		if(data.errors) {
			this.onLoginError("{{user.js.loginerr}}");
		} else if(data.response) {
			this.user = data.response;
			if(!this.user) {
				this.onLoginError("{{user.js.loginerr}} {{user.js.activateaccount}}");
			} else {
				this.initHistory();
				this.initView();
			}
		}
	},
	onLoginError: function(message) {
		if($.cookie("bookshelf-username")) {
			$.deleteCookie("bookshelf-username");
			$.deleteCookie("bookshelf-token");
		}
		this.initHistory();
		this.initView(null, null, message);
	},
	initHistory: function () {
		this.routesHit = 0;
        Backbone.history.start();
		Backbone.history.on('route', function(router, method) {
			this.routesHit++;
			
			route = new Route();
			route.save({
					source: this.history.length > 0 ? this.history[this.history.length-1].fragment : null,
					target: Backbone.history.fragment,
					username: this.user ? this.user.id : null
				}, {
					success: function (response) { 
						if(response.error) console.err(response.error); //FIXME TOSS ME
						//DO NOTHING
					}
			});
			app.history.push({
				method : method,
				fragment : Backbone.history.fragment
			});
		}, this);
	},
	initView: function (message, warning, error) {
		var self = this;
		viewLoader.load("HeaderView", function() { self.initHeader(); });
		viewLoader.load("MessageView", function() { self.initMessages(message, warning, error); });
    },
    initHeader: function() {
		this.headerView = new HeaderView();
		$("#header").html(this.headerView.render({showMenu: true}).el);
    },
    initMessages: function(message, warning, error) {
    	this.messageView = new MessageView();
        if(message) this.messageView.messages.push(message);
        if(warning) this.messageView.warnings.push(warning);
        if(error) this.messageView.errors.push(error);
        $('#messages').html(this.messageView.render().el);
	},
	clear: function () {
		this.currentView = null;
		viewLoader.clear();
		this.initView();
	},
	back: function() {
		if(this.routesHit > 1) {
			//more than one route hit -> user did not land to current page directly
			window.history.back();
		} else {
			//otherwise go to the home page. Use replaceState if available so
			//the navigation doesn't create an extra history entry
			this.navigate('/', {trigger:true, replace:true});
		}
	},
	getUser: function (callback) {
		return this.user;
	},
	isAdmin: function () {
		if(!$.cookie("bookshelf-username")) return false;
		return this.getUser() && this.getUser().admin === true;
	},
	renderView : function(className, View, headerOptions, viewOptions) {
		if(headerOptions)
			this.headerSelection = headerOptions.selection;
		
		//clear current view
		if(this.currentView) this.currentView.close();
		this.currentViewName = className;
		this.currentViewClass = View;
		
		//renders the view
		var self = this;
		viewLoader.load(className, function() {
			self.currentView = new self.currentViewClass();
			self.currentView.render(viewOptions);
			$("#content").html(self.currentView.el);
			self.currentView.delegateEvents();
		});
		
		//selects header
		if(this.headerView) {
			if(!headerOptions || headerOptions.rerender !== false)
				$("#header").html(this.headerView.render().el);
		}
		if(this.clearMessages && this.messageView) {
			this.messageView.clear();
			$('#messages').html(this.messageView.render().el);
		} else {
			this.clearMessages = true;
		}
	},
	rerenderView : function() {
		if(this.currentView) {
			var headerOptions = {rerender: true};
			this.renderView(this.currentViewName, this.currentViewClass, headerOptions, this.currentView.lastOptions);
		}
	},
	pushMessageAndNavigate: function(messageType, message, route) {
		if(this.messageView) {
			eval("this.messageView." + messageType + "s.push(message);");
			$('#messages').html(this.messageView.render().el);
		} 
		if(route != null) {
			this.clearMessages = false;
			this.navigate(route, {trigger:true});
		}
	},
});
