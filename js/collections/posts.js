App.Collections.Posts = Backbone.Collection.extend({
  model: App.Models.Posts,
  initialize: function(posts) {
    this.collection = posts;
  }
});
