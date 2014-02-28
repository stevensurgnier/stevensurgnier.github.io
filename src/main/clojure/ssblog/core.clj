;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.core
  (:require [clojure.java.shell :refer [sh]]
            [cheshire.core :refer [generate-string]])
  (:import [java.io File]
           [org.pegdown PegDownProcessor Extensions]))

;; config

(defn get-config
  ([] (get-config "project.clj"))
  ([f] (->> f
            slurp
            read-string
            (drop 3)
            (apply hash-map))))

(defn get-mdbuild-config
  ([] (get-mdbuild-config {}))
  ([{:keys [f k] :or {f "project.clj" k :mdbuild}}]
     (-> (get-config f) (get k))))

(defn get-orgbuild-config
  ([] (get-orgbuild-config {}))
  ([{:keys [f k] :or {f "project.clj" k :orgbuild}}]
     (-> (get-config f) (get k))))

;; io

(defn list-files [suffix path]
  (let [f (File. path)]
    (when (.isDirectory f)
      (filter #(.endsWith (str %) suffix) (.listFiles f)))))

(defn find-files-by-type [suffix paths]
  (mapcat (partial list-files suffix) paths))

(defn get-basename [^File file]
  (let [name (.getName file)
        pos (.lastIndexOf name ".")]
    (.substring name 0 pos)))

;; text processing

(defn split-file-on [marker content]
  (let [offset (inc (count marker))
        i (.indexOf content marker offset)]
    {:metadata (.substring content offset i)
     :body (.substring content (dec (+ offset i)))}))

(defn re-seq-to-map [re s]
  (reduce (fn [acc [_ k v]] (assoc acc (keyword k) v))
          {} (re-seq re s)))

;; process

(defn add-goog-preamble
  [{:keys [output-js-as]} js-string]
  (str "goog.provide(" "\"" output-js-as "\"" ");"
       output-js-as "=" js-string ";"))

(defn write-js
  [{:keys [output-js-to]} js-string]
  (println (format "Writing javascript file to %s" output-js-to))
  (spit output-js-to js-string))

(defn process-files
  [files-to-clj-fn config]
    (let [add-goog-preamble-fn (partial add-goog-preamble config)
          write-js-fn (partial write-js config)]
      (-> config
          files-to-clj-fn
          generate-string
          add-goog-preamble-fn
          write-js-fn)))

;; md

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

(def process-md-files
  (partial process-files md-files-to-clj))

;; main

(defn -main [& args]
  (println "Processing files")
  (process-md-files (get-mdbuild-config))
  (System/exit 0))
