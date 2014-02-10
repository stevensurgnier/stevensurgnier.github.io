goog.provide("ssblog.routers.App");

goog.require("backbone");

ssblog.routers.App = Backbone.Router.extend({
  routes: {
    "post/:id": "post",
    "*all": "default"
  }
});
