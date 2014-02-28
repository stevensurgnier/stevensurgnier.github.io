(defproject ssblog "0.0.1"
  :license {:name "MIT" :url "http://opensource.org/licenses/MIT"}
  :description "webblog"
  :url "http://github.com/stevensurgnier/stevensurgnier.github.io"
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.0.2"]
                 [org.pegdown/pegdown "1.4.2"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "0.3.2"]]}}
  :main ssblog.core
  :cljsbuild
  {:builds
   [{:source-paths ["src/main/cljs"]
     :compiler
     {:libs ["src/main/js"]
      :foreign-libs
      [{:file "http://zeptojs.com/zepto.min.js"
        :provides ["zepto"]}
       {:file "http://underscorejs.org/underscore-min.js"
        :provides ["underscore"]}
       {:file "http://backbonejs.org/backbone-min.js"
        :provides ["backbone"]}
       {:file "https://raw.github.com/mbostock/d3/v3.4.1/d3.min.js"
        :provides ["d3"]}]
      :pretty-print true
      :output-to "src/main/resources/public/js/ssblog.js"
      :optimizations :whitespace}}]}
  :mdbuild {:source-paths ["src/main/md"]
            :output-js-to "src/main/js/posts.js"
            :output-js-as "ssblog.posts"}
  :orgbuild {:emacs "/Applications/Emacs.app/Contents/MacOS/Emacs"
             :source-paths ["src/main/org"]
             :output-js-to "src/main/js/posts.js"
             :output-js-as "ssblog.posts"
             :org-load-path "~/.emacs.d/elpa/org-20140210/"
             :org-export-command
             '(progn
               (setq org-export-with-toc nil
                     org-export-with-section-numbers nil)
               (org-html-export-as-html nil nil nil t nil)
               (princ (buffer-string)))})
