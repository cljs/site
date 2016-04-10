(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [planck.core :refer [slurp spit]]
    [planck.io :refer [file-attributes]]
    [planck.shell :refer [sh]]))

(def api nil)

(defn slurp-url [url]
  (:out (sh "curl" url)))

(defn slurp-url-json [url]
  (-> (slurp-url url)
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(def versions-url "https://api.github.com/repos/cljsinfo/cljs-api-docs/tags")
(defn api-url [version] (str "https://raw.githubusercontent.com/cljsinfo/cljs-api-docs/" version "/cljs-api.edn"))
(defn api-filename [version] (str "cljs-api-" version ".edn"))

; keys here: https://developer.github.com/v3/repos/#list-tags
(defn get-latest-version []
  (println "Checking latest version...")
  (->> (slurp-url-json versions-url)
       (first)
       (:name)))

(defn set-from-download!
  [filename]
  (->> (slurp filename)
       (read-string)
       (set! api)))

(defn update! []
  (let [version (get-latest-version)
        filename (api-filename version)
        downloaded? (boolean (file-attributes filename))]
    (when-not downloaded?
      (println (str "Downloading latest API " version "..."))
      (->> (api-url version)
           (slurp-url)
           (spit filename)))
    (set-from-download! filename)))
