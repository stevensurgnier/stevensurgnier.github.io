/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.views.Posts");

goog.require("backbone");
goog.require("ssblog.views.PostLink");

ssblog.views.Posts = Backbone.View.extend({
  tagName: "div",
  className: "posts",
  initialize: function(options) {
  },
  render: function() {
    this.$el.empty();
    this.addPosts(this.model);
    return this;
  },
  addPost: function(post) {
    var view = new ssblog.views.PostLink({model: post});
    this.$el.append(view.render().el);
  },
  addPosts: function(posts) {
    posts.each(this.addPost, this);
  }
});
