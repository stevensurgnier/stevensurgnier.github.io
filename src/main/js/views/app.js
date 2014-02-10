goog.provide("ssblog.views.App");

goog.require("backbone");
goog.require("ssblog.views.Post");
goog.require("ssblog.views.Posts");

ssblog.views.App = Backbone.View.extend({
  el: "#app",
  initialize: function(options) {
    this.posts_collection = options.posts;
    this.posts_view = new ssblog.views.Posts({model: this.posts_collection});
  },
  render: function() {
    return this;
  },
  renderPosts: function() {
    this.$el.html(this.posts_view.render().el);
  },
  renderPost: function(id) {
    // TODO cache the view
    var post = this.posts_collection.findWhere({"id": id});
    var view = new ssblog.views.Post({model: post});
    this.$el.html(view.render().el);
  }
});
