$.ajaxSetup({
    statusCode: {
        401: function(){
			window.oldLocation = window.location.href;
            // Redirec the to the login page.
            window.location.replace('#login');
         
        },
        403: function() {
            // 403 -- Access denied
            window.location.replace('#denied');
        }
    }
});

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

		$.each(views, function(index, view) {
			if (window[view]) {
				deferreds.push($.get(getAppPath() + '/tpl/' + view + '.html', function(data) {
					window[view].prototype.template = _.template(data);
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
