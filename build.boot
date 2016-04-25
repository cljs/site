(set-env!
  :source-paths #{"src"}
  :dependencies
  '[[org.clojure/clojurescript "1.7.228"]
    [adzerk/boot-cljs "1.7.228-1"]
    [hiccups "0.3.0"]])

(require
  '[adzerk.boot-cljs :refer [cljs]])
