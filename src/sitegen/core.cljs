(ns sitegen.core
  (:require
    [planck.shell :refer [sh]]
    [planck.core :refer [spit]]
    [sitegen.api :as api]
    [sitegen.news :as news]
    [hiccups.runtime :refer [render-html]]))

(defn -main []
  (api/update!)
  (news/update!))
