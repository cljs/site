(ns sitegen.urls
  (:require
    [clojure.string :as string]
    [planck.core :refer [spit]]
    [planck.shell :refer [sh]]))

(def root "http://cljsinfo.github.io")

(def  home                            "/index.html")
(def  news-dir                        "/news/")
(def  news-index                      "/news/index.html")
(def  news-feed                       "/news/feed.xml")
(defn news-post  [title]         (str "/news/" title ".html"))
(def  docs-dir                        "/docs/")
(defn api-ns     [ns]            (str "/docs/" ns))
(defn api-symbol [ns sym-encode] (str "/docs/" ns "/" sym-encode ".html"))

(def out-dir "output")
(defn write! [url content] (spit (str out-dir url) content))
(defn make-dir! [url] (sh "mkdir" "-p" (str out-dir url)))

(defn pretty [url]
  (-> url
      (string/replace #"/index\.html$" "")
      (string/replace #"\.html$" "")))
