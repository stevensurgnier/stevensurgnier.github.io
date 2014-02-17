(ns ssblog.core
  (:require [clojure.java.shell :refer [sh]]
            [cheshire.core :refer [generate-string]])
  (:import (java.io File)))

(defn get-orgbuild-config
  ([] (get-orgbuild-config {}))
  ([{:keys [f k] :or {f "project.clj" k :orgbuild}}]
     (->> f
          slurp
          read-string
          (drop 3)
          (apply hash-map)
          k)))

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
  (reduce (fn [acc [_ property value]]
            (assoc acc (keyword property) value))
          {} (re-seq #"([^#\+]+): (.+)[\n|$]" (slurp file))))

(defn get-org-id [^File file]
  (let [name (.getName file)
        pos (.lastIndexOf name ".")]
    (.substring name 0 pos)))

(defn process-org-file
  [config ^File file]
  (let [html-body (org-to-html config file)
        id (get-org-id file)
        properties (get-org-properties file)]
    (merge {} properties {:id id} {:body html-body})))

(defn list-files [suffix path]
  (let [f (File. path)]
    (when (.isDirectory f)
      (filter #(.endsWith (str %) suffix) (.listFiles f)))))

(defn find-org-files [paths]
  (mapcat (partial list-files ".org") paths))

(defn org-files-to-clj
  [{:keys [source-paths] :as config}]
  (let [files (find-org-files source-paths)]
    (map (partial process-org-file config) files)))

(defn write-js
  [{:keys [output-js-to]} js-string]
  (spit output-js-to js-string))

(defn process-org-files
  [config]
  (let [save (partial write-js config)]
    (-> config
         org-files-to-clj
         generate-string
         save)))

(defn- main [& args]
  (process-org-files (get-orgbuild-config)))
