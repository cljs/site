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

(def  versions                         "/versions.html")

(def  api-dir                          "/api/")
(def  api-index                        "/api/index.html")
(defn api-ns     [ns]             (str "/api/" ns "/index.html"))
(defn api-symbol [ns name-encode] (str "/api/" ns "/" name-encode ".html"))
(defn api-compiler-ns [ns]        (str "/api/compiler/" ns "/index.html"))

(defn api-ns* [api-type ns]
  (case api-type
    :library (api-ns ns)
    :syntax (api-ns ns)
    :compiler (api-compiler-ns ns)
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
