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
(def  docs-index                  (str "/docs/index.html"))

(def  ref-dir                          "/ref/")
(defn ref-ns     [ns]             (str "/ref/" ns "/index.html"))
(defn ref-symbol [ns name-encode] (str "/ref/" ns "/" name-encode ".html"))
(defn ref-compiler-ns [ns]        (str "/ref/compiler/" ns "/index.html"))
(def  ref-index                   (str "/ref/index.html"))
(def  ref-history                 (str "/ref/history/index.html"))
(defn ref-version [version]       (str "/ref/history/" version ".html"))

(defn ref-api-ns [api-type ns]
  (case api-type
    :library (ref-ns ns)
    :syntax (ref-ns ns)
    :compiler (ref-compiler-ns ns)
    nil))

(def out-dir "output")
(defn write! [url content] (io/spit (str out-dir url) content))
(defn make-dir! [url]
  (let [url (if (string/ends-with? url "/index.html")
              (string/replace url "/index.html" "")
              url)]
    (io/mkdirs (str out-dir url))))

(defn pretty [url]
  (-> url
      (string/replace #"/index\.html$" "")
      (string/replace #"\.html$" "")))
