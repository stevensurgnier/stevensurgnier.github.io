/**
 * -*- coding: utf-8 -*-
 *
 * © 2014 Steven Surgnier
 * Author: Steven Surgnier <stevensurgnier@gmail.com>
 */

goog.provide("ssblog.routers.App");

goog.require("backbone");

ssblog.routers.App = Backbone.Router.extend({
  routes: {
    "post/:id": "post",
    "post/:id/:type/:n": "hotlink",
    "*all": "default"
  }
});
