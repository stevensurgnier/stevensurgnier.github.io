/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.collections.Posts");

goog.require("backbone");
goog.require("ssblog.models.Posts");

ssblog.collections.Posts = Backbone.Collection.extend({
  model: ssblog.models.Posts,
  initialize: function(posts) {
    this.collection = posts;
  }
});
