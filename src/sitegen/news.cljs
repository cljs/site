(ns sitegen.news
  (:require
    [cljs.reader :refer [read-string]]
    [planck.core :refer [slurp]]))

(def index [])
(def index-filename "news/index.edn")
(defn update-index! []
  (->> (slurp index-filename)
       (read-string)
       (set! index)))

(def posts {})
(def posts-dir "news/")
(defn update-posts! []
  (doseq [{:keys [filename]} index]
    (let [content (slurp (str posts-dir filename))]
      (set! posts (assoc posts filename content)))))

(defn update! []
  (update-index!)
  (update-posts!))
