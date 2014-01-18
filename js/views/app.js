App.Views.App = Backbone.View.extend({
  el: "#app",
  initialize: function(options) {
    this.posts_collection = options.posts;
    this.posts_view = new App.Views.Posts({model: this.posts_collection});
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
    var view = new App.Views.Post({model: post});
    this.$el.html(view.render().el);
  }
});
