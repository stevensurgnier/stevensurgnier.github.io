;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.util
  "Utility functions"
  (:require [cheshire.core :refer [generate-string]])
  (:import [java.io File]))

;; config

(defn get-config
  ([] (get-config "project.clj"))
  ([f] (->> f
            slurp
            read-string
            (drop 3)
            (apply hash-map))))

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
