;; -*- coding: utf-8 -*-
;;
;; © 2014 Steven Surgnier
;; Author: Steven Surgnier <stevensurgnier@gmail.com>
;;

(ns ssblog.org
  "Functions to work with org-mode files"
  (:require [ssblog.util :refer [get-basename re-seq-to-map
                                 find-files-by-type process-files
                                 get-config]]
            [clojure.java.shell :refer [sh]])
  (:import [java.io File]))

(defn get-orgbuild-config
  ([] (get-orgbuild-config {}))
  ([{:keys [f k] :or {f "project.clj" k :orgbuild}}]
     (-> (get-config f) (get k))))

(defn org-to-html
  [{:keys [emacs org-load-path org-export-command]} ^File file]
  (let [file-path (.getAbsolutePath file)
        org-load-elisp `(~'progn
                         (~'add-to-list '~'load-path ~org-load-path)
                         (~'require '~'org))
        org-export-elisp `(~'progn
                           (~'find-file ~file-path)
                           ~(eval org-export-command))
        result (sh emacs "--batch"
                   "--eval" (str org-load-elisp)
                   "--eval" (str org-export-elisp))]
    (get result :out)))

(defn get-org-properties [^File file]
  (re-seq-to-map #"([^#\+]+): (.+)[\n|$]" (slurp file)))

(def get-org-id get-basename)

(defn process-org-file
  [config ^File file]
  (println (format "Processing %s" file))
  (let [id (get-org-id file)
        properties (get-org-properties file)
        html-body (org-to-html config file)]
    (merge {:id id} properties {:body html-body})))

(def find-org-files (partial find-files-by-type ".org"))

(defn org-files-to-clj
  [{:keys [source-paths] :as config}]
  (let [files (find-org-files source-paths)]
    (map (partial process-org-file config) files)))

(defn process-org-files
  ([] (process-org-files (get-orgbuild-config)))
  ([config] (process-files org-files-to-clj config)))
