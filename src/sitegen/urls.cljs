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
(def  ref-dir                          "/ref/")
(defn ref-ns     [ns]             (str "/ref/" ns))
(defn ref-symbol [ns name-encode] (str "/ref/" ns "/" name-encode ".html"))
(defn ref-compiler-ns [ns]        (str "/ref/compiler/" ns))
(def  ref-index                   (str "/ref/index.html"))
(def  ref-history                 (str "/ref/history/index.html"))
(def  ref-version                 (str "/ref/history/version.html"))

(def out-dir "output")
(defn write! [url content] (io/spit (str out-dir url) content))
(defn make-dir! [url] (io/mkdirs (str out-dir url)))

(defn pretty [url]
  (-> url
      (string/replace #"/index\.html$" "")
      (string/replace #"\.html$" "")))
