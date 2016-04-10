(ns sitegen.news
  (:require
    [cljs.reader :refer [read-string]]
    [planck.core :refer [spit slurp]]
    [sitegen.html.common :refer [common-layout]]
    [sitegen.html.news-index :as news-index]
    [sitegen.html.news-post :as news-post]
    [hiccups.runtime :refer [render-html]]))

(def posts [])
(def index-filename "news/index.edn")
(def content-dir "news/")

(defn update-index! []
  (->> (slurp index-filename)
       (read-string)
       (set! posts)))

(defn update-content! []
  (set! posts
    (for [post posts]
      (let [filename (str content-dir (:filename post))
            md-content (slurp filename)]
        (assoc post :md-content md-content)))))

(defn update! []
  (update-index!)
  (update-content!))

(defn create-index-page!
  [out-filename]
  (->> (news-index/content index)
       (common-layout)
       (render-html)
       (spit out-filename)))

(defn create-post-pages!
  [out-dir]
  (->> (news-post/content post)
       (common-layout)
       (render-html)
       (spit out-filename)))
