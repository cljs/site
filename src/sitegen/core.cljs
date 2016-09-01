(ns sitegen.core
  (:require
    [sitegen.api-pages :as api-pages]
    [sitegen.api :as api]
    [sitegen.news :as news]
    [sitegen.home :as home]
    [sitegen.versions :as versions]
    [sitegen.docset :as docset]))

(defn -main []
  (api/update!)
  (api/check!)

  (news/update!)

  (home/render!)
  (news/render!)
  (versions/render!)

  ;(api-pages/render!)
  (docset/create!))

(set! *main-cli-fn* -main)
(enable-console-print!)
