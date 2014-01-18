window.App = {
  Models: {},
  Collections: {},
  Views: {},
  Routers: {},
  init: function(posts) {
    this.app_view = new this.Views.App({
      posts: posts
    });
    console.log("init", this.app_view);
    this.initRouter(this.app_view);
  },
  initRouter: function(app_view) {
    console.log("initRouter", app_view);

    var router = new App.Routers.App;
    router.on("route:post", function(id) {
      console.log("route post", id);
      app_view.renderPost(id);
    });
    
    router.on("route:default", function(options) {
      console.log("route default", options);
      app_view.renderPosts();
    });

    Backbone.history.start();
  }
};

$(document).ready(function() {
  var posts = new App.Collections.Posts([
    {"id": "dct",
     "title": "Discrete Cosine Transform",
     "date": "2014-01-18",
     "template": "#template-post-dct"
    },
    {"id": "fft",
     "title": "Fast Fourier Transform",
     "date": "2014-02-18",
     "template": "#template-post-fft"
    }
  ]);

  App.init(posts);
});
