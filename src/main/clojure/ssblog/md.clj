;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.md
  "Functions to work with markdown files"
  (:require [ssblog.util :refer [get-basename re-seq-to-map
                                 find-files-by-type process-files
                                 get-config split-file-on]])
  (:import [java.io File]
           [org.pegdown PegDownProcessor Extensions]))

(defn get-mdbuild-config
  ([] (get-mdbuild-config {}))
  ([{:keys [f k] :or {f "project.clj" k :mdbuild}}]
     (-> (get-config f) (get k))))

(def extensions
  [Extensions/FENCED_CODE_BLOCKS Extensions/TABLES])

(def peg-down-processor
  (PegDownProcessor. (int (apply bit-or extensions))))

(def split-file-on-triple-dash
  (partial split-file-on "---"))

(defn split-md-into-metadata-and-body
  [^File file]
  (-> file slurp split-file-on-triple-dash))

(defn get-md-body
  [^File file]
  (:body (split-md-into-metadata-and-body file)))

(defn md-to-html
  [^File file]
  (.markdownToHtml peg-down-processor (get-md-body file)))

(defn get-md-metadata
  [^File file]
  (:metadata (split-md-into-metadata-and-body file)))

(defn get-md-properties
  [^File file]
  (re-seq-to-map #"(.+): (.+)[\n|$]" (get-md-metadata file)))

(def get-md-id get-basename)

(def find-md-files (partial find-files-by-type ".md"))

(defn process-md-file
  [^File file]
  (println (format "Processing %s" file))
  (let [id (get-md-id file)
        properties (get-md-properties file)
        html-body (md-to-html file)]
    (merge {:id id} properties {:body html-body})))

(defn md-files-to-clj
  [{:keys [source-paths] :as config}]
  (let [files (find-md-files source-paths)]
    (map process-md-file files)))

(defn process-md-files
  ([] (process-md-files (get-mdbuild-config)))
  ([config] (process-files md-files-to-clj config)))
