App.Models.Posts = Backbone.Model.extend({
  defaults: function() {
    return {
      title: "",
      date: ""
    };
  }
});
