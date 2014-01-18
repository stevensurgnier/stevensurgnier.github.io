App.Views.Posts = Backbone.View.extend({
  tagName: "div",
  className: "posts",
  initialize: function(options) {
    console.log("posts", this.model);
  },
  render: function() {
    this.$el.empty();
    this.addPosts(this.model);
    return this;
  },
  addPost: function(post) {
    var view = new App.Views.PostLink({model: post});
    this.$el.append(view.render().el);
  },
  addPosts: function(posts) {
    posts.each(this.addPost, this);
  }
});
