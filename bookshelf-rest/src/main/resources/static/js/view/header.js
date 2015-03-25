window.HeaderView = Backbone.View.extend({
	
    initialize: function () {
    },

    render: function () {
        $(this.el).html(this.template());
        return this;
    },

    events: {
        "keyup .search-query": "search",
        "keypress .search-query": "onkeypress",
		"onclick .logout-menu": "logout"
    },

    search: function () {
        var key = $('#searchText').val();
        console.log('search ' + key);
    },

    onkeypress: function (event) {
        if (event.keyCode == 13) {
            event.preventDefault();
        }
    },

    select: function(menuItem) {
    	this.selection = menuItem;
        $('.nav li').removeClass('active');
        $('.' + menuItem).addClass('active');
    }
});