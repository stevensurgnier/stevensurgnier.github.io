/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.models.Posts");

goog.require("backbone");

ssblog.models.Posts = Backbone.Model.extend({
  defaults: function() {
    return {
      id: "",
      title: "",
      date: "",
      tags: "",
      body: ""
    };
  }
});
