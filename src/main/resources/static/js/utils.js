$.ajaxSetup({
    statusCode: {
        401: function(){
			window.oldLocation = window.location.href;
            // Redirect the to the login page.
            window.location.replace('#login');
         
        },
        403: function() {
            // 403 -- Access denied
            window.location.replace('#denied');
        }
    }
});

window.createToken = function (username, password) {
//	return CryptoJS.SHA1(username + ":-1:" + password + ":bookshelf by Samvise85!").toString(CryptoJS.enc.Hex);
	return CryptoJS.SHA1(username + ":-1:" + CryptoJS.SHA1(password).toString(CryptoJS.enc.Hex) + ":bookshelf by Samvise85!").toString(CryptoJS.enc.Hex);
}
$.getToken = function () {
	if($.cookie('bookshelf-username') && $.cookie('bookshelf-token'))
		return {'bookshelf-username': $.cookie('bookshelf-username'), 'bookshelf-token': $.cookie('bookshelf-token')};
	return {};
}
$.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
	if(options.url.indexOf('http') != 0)
		options.url = getAppPath() + options.url;
	options.headers = $.getToken();
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

window.getAppPath = function() {
	if(!window.appname) {
		var name = window.location.pathname.split("/")[1];
		window.appname = name.toLowerCase().indexOf("bookshelf") >= 0 ? name : null;
		
	}
	return '//' + window.location.host + (window.appname != null ? "/" + window.appname : "");
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
						window[view].prototype.template = _.template(data);
					} catch(e) {
						console.error("Error loading template of view " + view);
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

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

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
