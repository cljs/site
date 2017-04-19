(ns sitegen.urls
  (:require
    [clojure.string :as string]
    [util.io :as io]))

(def md5 (js/require "md5"))

(def domain "http://cljsinfo.github.io")

;; All URLs are absolute paths, but the docset requires relative paths, so
;; we use this to generate a relative path to the root which we can prepend to
;; all generated links.
(def ^:dynamic *root* "")
(defn get-root [url]
  (let [n (dec (get (frequencies url) "/"))]
    (if (pos? n)
      (string/join "/" (repeat n ".."))
      ".")))

(def ^:dynamic *case-sensitive* true)
(defn protect-case [filename]
  (if-not *case-sensitive*
    (str filename "-" (md5 filename))
    filename))

(def ^:dynamic *pretty-links* true)
(defn pretty [url]
  (if *pretty-links*
    (-> url
        (string/replace #"/index\.html" "")
        (string/replace #"\.html" ""))
    url))

(def  home                                  "/index.html")
(def  _404                                  "/404.html")

(def  news-dir                              "/news/")
(def  news-index                            "/news/index.html")
(def  news-feed                             "/news/feed.xml")
(defn news-post  [title]               (str "/news/" title ".html"))

(def  versions                              "/versions.html")

(def  api-dir                               "/api/")
(def  api-index                             "/api/index.html")
(defn api-ns          [ns]             (str "/api/" ns "/index.html"))
(defn api-sym         [ns name-encode] (str "/api/" ns "/" (protect-case name-encode) ".html"))
(defn api-compiler-ns [ns]             (str "/api/compiler/" ns "/index.html"))

(defn api-ns* [api-type ns]
  (case api-type
    :library (api-ns ns)
    :syntax (api-ns ns)
    :options (api-ns ns)
    :compiler (api-compiler-ns ns)
    nil))

(defn api-sym-prev [api-type ns name-encode]
  (str (pretty (api-ns* api-type ns)) "#" name-encode))

(def ^:dynamic *out-dir* "output")
(defn write! [url content] (io/spit (str *out-dir* url) content))
(defn make-dir! [url]
  (let [url (if (string/ends-with? url "/index.html")
              (string/replace url "/index.html" "")
              url)
        path (str *out-dir* url)]
    (io/mkdirs path)))
