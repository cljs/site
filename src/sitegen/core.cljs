(ns sitegen.core
  (:require
    [planck.shell :refer [sh]]
    [planck.core :refer [spit]]
    [sitegen.api :as api]
    [sitegen.pages.index :as index]
    [hiccups.runtime :refer [render-html]]))

(defn -main []
  (api/update!))
