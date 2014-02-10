(defproject ssblog "0.0.1"
  :description "webblog"
  :url "http://github.com/stevensurgnier/stevensurgnier.github.io"
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :resource-paths ["src/main/resources"]
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [hiccup "1.0.4"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "0.3.2"]]}}
  :main ssblog.core
  :hooks [leiningen.cljsbuild]
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
      :optimizations :whitespace}}]})
