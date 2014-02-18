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
  (println (format "Processing %s" file))
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

(defn add-goog-preamble
  [{:keys [output-js-as]} js-string]
  (str "goog.provide(" "\"" output-js-as "\"" ");"
       output-js-as "=" js-string ";"))

(defn write-js
  [{:keys [output-js-to]} js-string]
  (println (format "Writing javascript file to %s" output-js-to))
  (spit output-js-to js-string))

(defn process-org-files
  [config]
  (println "Processing org files")
  (let [add-goog-preamble-fn (partial add-goog-preamble config)
        write-js-fn (partial write-js config)]
    (-> config
        org-files-to-clj
        generate-string
        add-goog-preamble-fn
        write-js-fn)
    nil))

(defn -main [& args]
  (process-org-files (get-orgbuild-config))
  (System/exit 0))
