(ns sitegen.core
  (:require
    [sitegen.ref :as ref]
    [sitegen.news :as news]
    [sitegen.home :as home]))

(defn -main []
  (ref/update!)
  (news/update!)

  (home/render!)
  (news/render!)
  (ref/render!))

(set! *main-cli-fn* -main)
(enable-console-print!)
