(ns sitegen.core
  (:require
    [sitegen.api :as api]
    [sitegen.news :as news]
    [sitegen.home :as home]))

(defn -main []
  (api/update!)
  (news/update!)

  (home/render!)
  (news/render!)
  (api/render!))

(set! *main-cli-fn* -main)
(enable-console-print!)
