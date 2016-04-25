(ns sitegen.urls
  (:require
    [clojure.string :as string]
    [util.io :as io]))

(def root "http://cljsinfo.github.io")

(def  home                             "/index.html")
(def  news-dir                         "/news/")
(def  news-index                       "/news/index.html")
(def  news-feed                        "/news/feed.xml")
(defn news-post  [title]          (str "/news/" title ".html"))
(def  docs-dir                         "/docs/")
(defn api-ns     [ns]             (str "/docs/" ns))
(defn api-symbol [ns name-encode] (str "/docs/" ns "/" name-encode ".html"))
(def  api-index                   (str "/docs/index.html"))

(def out-dir "output")
(defn write! [url content] (io/spit (str out-dir url) content))
(defn make-dir! [url] (io/mkdirs (str out-dir url)))

(defn pretty [url]
  (-> url
      (string/replace #"/index\.html$" "")
      (string/replace #"\.html$" "")))
