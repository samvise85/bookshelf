window.HeaderView = Backbone.View.extend({
	
    initialize: function () {
    },

    render: function () {
        $(this.el).html(this.template());
    	this.select(app.headerSelection);
    	return this;
    },

    select: function(menuItem) {
    	this.selection = menuItem;
        $('.nav li', this.el).removeClass('active');
        if(menuItem)
        	$('.' + menuItem, this.el).addClass('active');
    }
});