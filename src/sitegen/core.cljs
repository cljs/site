(ns sitegen.core
  (:require
    [planck.shell :refer [sh]]
    [planck.core :refer [spit]]
    [sitegen.api :as api]
    [sitegen.news :as news]
    [sitegen.home :as home]
    [hiccups.runtime :refer [render-html]]))

(def out-dir "output/")

(defn create-pages! []
  (home/create-page! (str out-dir "index.html")))

(defn -main []
  (api/update!)
  (news/update!)
  (create-pages!))
