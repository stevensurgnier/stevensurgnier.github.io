/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.views.PostLink");

goog.require("backbone");

ssblog.views.PostLink = Backbone.View.extend({
  initialize: function(options) {
    console.log("post link", this.model);
  },
  render: function() {
    var link = "#/post/" + this.model.get("id");
    this.$el.html("<a href='" + link + "'>" + this.model.get("title") +
                  "</a>");
    return this;
  }
});
