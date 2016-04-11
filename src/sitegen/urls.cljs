(ns sitegen.urls
  (:require
    [clojure.string :refer [ends-with?]]
    [planck.core :refer [spit]]))

(def home "index.html")
(def news-index "news.html")
(defn news-post [title] (str "news/" title ".html"))

(def out-dir "output/")
(defn write! [url content]
  (spit (str out-dir url) content))

(defn pretty [url]
  (if (ends-with? url ".html")
    (subs url 0 (- (count url) (count ".html")))
    url))
