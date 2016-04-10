(ns sitegen.news
  (:require
    [cljs.reader :refer [read-string]]
    [planck.core :refer [slurp]]))

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
