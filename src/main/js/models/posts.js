goog.provide("ssblog.models.Posts");

goog.require("backbone");

ssblog.models.Posts = Backbone.Model.extend({
  defaults: function() {
    return {
      title: "",
      date: ""
    };
  }
});
