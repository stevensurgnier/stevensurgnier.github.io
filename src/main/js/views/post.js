goog.provide("ssblog.views.Post");

goog.require("backbone");

ssblog.views.Post = Backbone.View.extend({
  tagName: "div",
  className: "post",
  initialize: function(options) {
  },
  render: function() {
    this.$el.html(this.model.get("body"));
    return this;
  }
});
