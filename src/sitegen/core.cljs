(ns sitegen.core
  (:require
    [sitegen.api :as api]
    [sitegen.news :as news]
    [sitegen.home :as home]))

(defn -main []
  (api/update!)
  (news/update!)

  (home/create-page!       "output/index.html")
  (news/create-index-page! "output/news.html")
  (news/create-post-pages! "output/news/"))
