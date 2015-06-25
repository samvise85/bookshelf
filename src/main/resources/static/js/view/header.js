window.HeaderView = Backbone.View.extend({
	
    initialize: function () {
    },

    render: function () {
        $(this.el).html(this.template());
        if(app.headerSelection)
        	this.select(app.headerSelection);
    	return this;
    },

    select: function(menuItem) {
    	this.selection = menuItem;
        $('.nav li', this.el).removeClass('active');
        $('.' + menuItem, this.el).addClass('active');
    }
});