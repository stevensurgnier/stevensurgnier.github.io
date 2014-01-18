App.Views.Post = Backbone.View.extend({
  tagName: "div",
  className: "post",
  initialize: function(options) {
    this.template = _.template($(this.model.get("template")).html());
  },
  render: function() {
    this.$el.html(this.template(this.model.attributes));
    return this;
  }
});
