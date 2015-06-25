window.LanguageListView = Backbone.View.extend({
	reload : true,
	clearMessages : true,

	initialize: function() {
		var self = this;
		viewLoader.load("LanguageListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	render: function() {
		$(this.el).html(this.template());
		this.appendWhenReady();
		return this;
	},
	appendWhenReady: function() {
		if(this.listItemViewReady) {
			this.append();
		} else {
			var self = this;
			setTimeout(function () {
				self.appendWhenReady();
			}, 100);
		}
	},
	append: function() {
		var self = this;
		var languages = new Languages();
		languages.fetch({
			success: function(languages) {
				_.each(languages.models, function (language) {
					$('table tbody', self.el).append(new LanguageListItemView(language).render().el);
				}, self);
				return self;
			},
			error: function () {
				$(self.el).empty();
				$(self.el).html(self.template());
				return self;
			}
		});
	}
});

window.LanguageListItemView = Backbone.View.extend({
	tagName:"tr",
	saving: false,

    initialize:function (label) {
		this.model = label;
    },
	events: {
		'click a.setdefault': 'setDefault'
	},
	render: function (unset) {
		if(unset)
			this.unsetOldDef();
		$(this.el).html(this.template({language: this.model}));
		$(this.el).attr('def', this.model.get('def'));
        return this;
	},
	setDefault: function(ev) {
		ev.preventDefault();
		this.model.set('def', true);
		var self = this;
		self.model.save(null, {
			success: function (lang) {
				self.render(true);
			},
			error: function (req, resp) {
				if(resp.status != 200) {
					app.messageView.errors.push("An error occurred updating " + self.model.id);
					app.messageView.rerender();
					self.render();
				} else {
					self.render(true);
				}
			}
		});
	},
	unsetOldDef: function() {
		var oldDef = $(this.el).parent().find('tr[def=true]');
		if(oldDef) {
			oldDef.attr('def', false);
			var oldFlag = oldDef.find('td.default');
			oldFlag.empty();
			oldFlag.toggleClass('default');
			$('a.setdefault', this.el).appendTo(oldDef.find('td.setdefault'));
		}
	}
});
    
window.LabelListView = Backbone.View.extend({
	lastOptions : null,
	reload : false,
	clearMessages : true,
	page: 1,
	stopScroll: false,

	initialize: function() {
		var self = this;
		viewLoader.load("LabelListItemView", function() {
			self.listItemViewReady = true;
		});
	},
	newOptions: function(options) {
		return !arraysEqual(this.lastOptions, options);
	},
	render: function (options) {
		this.lastOptions = options;
		$(this.el).empty();
		$(this.el).html(this.template({view: this}));
		this.appendWhenReady(options);
	},
	appendWhenReady: function (options) {
//		console.log("Ready:"+ this.listItemViewReady);
		if(this.listItemViewReady) {
			this.append(options);
		} else {
			var self = this;
			setTimeout(function () {
				self.appendWhenReady(options);
			}, 100);
		}
	},
	append: function (options) {
		if(!(this.stopScroll === true)) {
			var self = this;
			var labels = new Labels();
			labels.fetch({
				data: $.param({"page": self.page, "language": options.lang}),
				success: function(labels) {
					if(labels.models.length == 0) self.stopScroll = true;
					_.each(labels.models, function (label) {
						$('table tbody', self.el).append(new LabelListItemView(label).render().el);
					}, self);
					return self;
				},
				error: function () {
					$(self.el).empty();
					$(self.el).html(self.template());
					return self;
				}
			});
			this.page++;
		}
	}
});

window.LabelListItemView = Backbone.View.extend({
	tagName:"tr",
	saving: false,

    initialize:function (label) {
		this.model = label;
    },
    events: {
    	"click .save-label": "updateLabel",
    	"blur input": "updateLabel",
    	"keyup input": "updateLabelKeyUp",
    },
    updateLabelKeyUp: function(ev) {
    	if(ev.keyCode == 13)
    		this.updateLabel(ev);
    },
	updateLabel: function (ev) {
		ev.preventDefault();
		if(!this.saving) {
			this.saving = true;
			var newval = $('[id="label-'+this.model.id+'"]').val();
			if(this.model.get('label') != newval) {
				$('[id="label-' + this.model.id + '-button"]').html(this.loading);
				this.model.set('label', newval);
			}
		}
	},
	saveLabel: function() {
		var self = this;
        this.model.unbind("change");
		self.model.save(null, {
			success: function () {
				self.render();
			},
			error: function (req, resp) {
				if(resp.status != 200) {
					app.messageView.errors.push("An error occurred updating " + self.model.id);
					app.messageView.rerender();
					self.render();
				} else {
					self.render();
				}
			}
		});
		return false;
	},
	render: function () {
		$(this.el).html(this.template({label: this.model}));
		this.button = $('[id="label-' + this.model.id + '-button"] a', this.el);
		this.loading = $('[id="label-' + this.model.id + '-button"] img', this.el);
		this.loading.remove();
        this.model.bind("change", this.saveLabel, this);
        this.saving = false;
        return this;
	}
});
