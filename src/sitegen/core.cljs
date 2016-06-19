(ns sitegen.core
  (:require
    [sitegen.docs :as docs]
    [sitegen.ref :as ref]
    [sitegen.api :as api]
    [sitegen.news :as news]
    [sitegen.home :as home]))

(defn -main []
  (api/update!)
  (news/update!)

  (home/render!)
  (news/render!)
  (docs/render!)
  (ref/render!))

(set! *main-cli-fn* -main)
(enable-console-print!)
