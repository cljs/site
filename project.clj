(defproject site "0.1.0"
  :description "Static site for ClojureScript"
  :url "https://github.com/cljsinfo"
  :dependencies  [[org.clojure/clojure "1.7.0"]
                  [org.clojure/clojurescript "1.7.48" :classifier "aot"
                   :exclusion  [org.clojure/data.json]]
                  [org.clojure/data.json "0.2.6" :classifier "aot"]]
  :jvm-opts ^:replace  ["-Xmx1g" "-server"]
  :plugins  [[lein-npm "0.6.1"]]
  :npm  {:dependencies  [[source-map-support "0.3.2"]]}
  :source-paths  ["src/clj" "src/cljs" "target/classes"]
  :clean-targets  ["out" "release"]
  :target-path "target"

  :main site.core)
