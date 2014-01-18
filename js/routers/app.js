App.Routers.App = Backbone.Router.extend({
  routes: {
    "post/:id": "post",
    "*all": "default"
  }
});
