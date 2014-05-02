;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.core
  "CLI"
  (:require [ssblog.md :refer [process-md-files]]))

(defn -main [& args]
  (println "Processing files")
  (process-md-files)
  (System/exit 0))
