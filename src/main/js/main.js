/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.App");

goog.require("zepto");
goog.require("underscore");
goog.require("backbone");

goog.require("ssblog.posts");
goog.require("ssblog.routers.App");
goog.require("ssblog.collections.Posts");
goog.require("ssblog.views.App");

$(document).ready(function() {

  // init posts collection
  var posts = new ssblog.collections.Posts(ssblog.posts);

  // init app view
  var app_view = new ssblog.views.App({
    posts: posts
  });

  var renderPost = function(id) {
    app_view.renderPost(id);
  }

  // init router
  var router = new ssblog.routers.App;

  router.on("route:post", function(id) {
    renderPost(id);
  });

  router.on("route:hotlink", function(id, type, n) {
    renderPost(id);
    var el = document.getElementById(type + "-" + n);
    el.scrollIntoView(true);
  });

  router.on("route:default", function(options) {
    app_view.renderPosts();
  });

  // init history
  Backbone.history.start();
});
