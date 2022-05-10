(ns sitegen.urls
  (:require
    [clojure.string :as string]
    [sitegen.state :refer [*docset?*]]
    [me.raynes.fs :as fs]))

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

(defn index-lookup-map
  "convert vector to an index lookup map. e.g. [:foo :bar] -> {:foo 0, :bar 1}"
  [v]
  (into {} (map-indexed #(vector %2 %1) v)))

(def ^:dynamic *case-sensitive* true)
(def case-collisions (atom nil))
(defn set-case-collisions! [api]
  (reset! case-collisions
    (->> (vals (:symbols api))
         (map :full-name-encode)
         (group-by #(.toLowerCase %))
         (keep (fn [[k v]] (when (< 1 (count v)) [k (index-lookup-map v)])))
         (into {}))))

(defn protect-case [ns name]
  (if *case-sensitive*
    name
    (let [full-name (str ns "/" name)]
      (if-let [index-of (@case-collisions (.toLowerCase full-name))]
        (str name "-" (index-of full-name))
        name))))

(def ^:dynamic *pretty-links* true)
(defn pretty [url]
  (if *pretty-links*
    (-> url
        (string/replace #"/index\.html" "")
        (string/replace #"\.html" ""))
    url))

(def  home                                  "/index.html")
(def  _404                                  "/404.html")

(def  versions                              "/versions.html")

(def  api-dir                               "/api/")
(def  api-index                             "/api/index.html")
(defn api-ns          [ns]             (str "/api/" ns "/index.html"))
(defn api-sym         [ns name-encode] (str "/api/" ns "/"
                                         (if *docset?* (str ns " ") "") ; put ns in filename for Dash search results
                                         (protect-case ns name-encode) ".html"))
(defn api-compiler-ns [ns]             (str "/api/compiler/" ns "/index.html"))

(defn api-item
  [{:keys [ns name-encode type]}]
  (if (= type "namespace")
    (api-ns ns)
    (api-sym ns name-encode)))

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
(defn write! [url content] (spit (str *out-dir* url) content))
(defn make-dir! [url]
  (let [url (if (string/ends-with? url "/index.html")
              (string/replace url "/index.html" "")
              url)
        path (str *out-dir* url)]
    (fs/mkdirs path)))
