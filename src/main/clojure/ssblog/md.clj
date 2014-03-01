;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.md
  "Functions to work with markdown files"
  (:require [ssblog.util :refer [get-basename re-seq-to-map
                                 find-files-by-type process-files
                                 get-config split-file-on]]
            [hiccup.core :as html]
            [clojure.string :as string])
  (:import [java.io File]
           [org.pegdown PegDownProcessor Extensions]))

(defn get-whole-number [n]
  (first (string/split n #"\." 2)))

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
  [md]
  (-> md split-file-on-triple-dash))

(defn get-md-body
  [md]
  (:body (split-md-into-metadata-and-body md)))

(defn get-md-metadata
  [md]
  (:metadata (split-md-into-metadata-and-body md)))

(defn md-to-html
  [md-body]
  (.markdownToHtml peg-down-processor md-body))

(def get-md-properties
  (partial re-seq-to-map #"(.+): (.+)[\n|$]"))

#_(def get-md-id get-basename)

(def find-md-files (partial find-files-by-type ".md"))

#_(defn process-md-file
  [^File file]
  (println (format "Processing %s" file))
  (let [id (get-md-id file)
        properties (get-md-properties file)
        html-body (md-to-html file)]
    (merge {:id id} properties {:body html-body})))

(defn files-to-strings
  [files]
  (map slurp files))

(defn get-nid-map
  "Accept a sequence of md property maps. Return a map from :n to :id."
  [seq-md-properties]
  (->> seq-md-properties
       (map #(hash-map (:n %) (:id %)))
       (into {})))

(defn href-with-root
  [root & paths]
  (string/join "/" (list* root paths)))

(def post-href (partial href-with-root "#/post"))

;; sections

(defn section-to-html
  [id [symbols number title]]
  (let [n (count symbols)]
    (html [:a {:class "sec" :id (str "sec" "-" number)
               :href (post-href id "sec" number)}
           [(keyword (str "h" n)) (str number " " title)] ])))

(defn preprocess-sections
  [{:keys [nid-map body properties]}]
  (let [{:keys [id]} properties
        matches (re-seq #"(#+) ([\d\.]+) ([\w ]+)" body)
        args (map rest matches)]
    (map (partial section-to-html id) args)))

;; special-elements

(defn preprocess-equation
  [id [element body]]
  (when-let [match (re-find #"([\d\.]+) (.+)" body)]
    (let [[_ number equation] match]
      {:element element
       :number number
       :equation equation})))

(defn preprocess-figure
  [id [element body]]
  (when-let [match (re-find #"([\d\.]+) ([\w://\.]+) \[([\w ]+)\] \[([\w ]+)\]" body)]
    (let [[_ number link title footer] match]
      {:element element
       :number number
       :title title
       :link link
       :footer footer})))

(defn preprocess-table
  [id [element body]]
  (when-let [match (re-find #"(?s)([\d\.]+) \[([^\]]+)\] \[([\w ]+)\] \[([\w ]+)\]" body)]
    (let [[_ number table title footer] match]
      {:element element
       :number number
       :table table
       :title title
       :footer footer})))

(defn preprocess-code
  [id [element body]]
  (when-let [match (re-find #"(?s)([\d\.]+) \[([^\]]+)\] \[([\w ]+)\] \[([\w ]+)\]" body)]
    (let [[_ number code title footer] match]
      {:element element
       :number number
       :code code
       :title title
       :footer footer})))

(defn preprocess-special-element
  [id element-and-body]
  (case (first element-and-body)
      "eq" (preprocess-equation id element-and-body)
      "fig" (preprocess-figure id element-and-body)
      "table" (preprocess-table id element-and-body)
      "code" (preprocess-code id element-and-body)
      nil))

(defn preprocess-special-elements
  [{:keys [nid-map body properties]}]
  (let [{:keys [id]} properties
        preprocess (partial preprocess-special-element id)
        matches (re-seq #"(?s)\{\{(\w+) ([^\}]+)\}\}" body)
        args (map rest matches)]
    (map preprocess args)))

;; hotlinks

(defn hotlink-to-html
  [id [type number]]
  (html [:a {:class (str "hotlink" " " type)
             :href (post-href id type number)}
         number]))

(defn get-hotlink-id
  [nid-map number]
  (get nid-map (get-whole-number number)))

(defn get-hotlink-ids
  [nid-map numbers]
  (map (partial get-hotlink-id nid-map) numbers))

(defn preprocess-hotlinks
  [{:keys [nid-map body properties]}]
  (let [matches (re-seq #"!\[(\w+) ([\d\.]+)\][^\(]" body)
        args (map rest matches)
        ids (get-hotlink-ids nid-map (map second args))]
    (map hotlink-to-html ids args)))

(defn preprocess-md
  [nid-map body properties]
  (-> {:nid-map nid-map :body body :properties properties}
      preprocess-sections
      preprocess-special-elements
      preprocess-hotlinks))

(defn md-files-to-clj
  [{:keys [source-paths] :as config}]
  (let [files (find-md-files source-paths)
        str-files (files-to-strings files)
        seq-metadata-and-body (map split-md-into-metadata-and-body str-files)
        seq-metadata (map :metadata seq-metadata-and-body)
        seq-body (map :body seq-metadata-and-body)
        seq-properties (map get-md-properties seq-metadata)
        nid-map (get-nid-map seq-properties)]
    (map (partial preprocess-md nid-map) seq-body seq-properties)))

(defn process-md-files
  ([] (process-md-files (get-mdbuild-config)))
  ([config] (process-files md-files-to-clj config)))
