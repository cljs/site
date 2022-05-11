(ns sitegen.core
  (:require
    [sitegen.api-pages :as api-pages]
    [sitegen.api :as api]
    [sitegen.home :as home]
    [sitegen.versions :as versions]
    [sitegen.docset :as docset]))

(defn -main []
  (api/update!)

  (home/render!)
  (versions/render!)

  (api-pages/render!)
  (docset/create!))

