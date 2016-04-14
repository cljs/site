(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [planck.core :refer [slurp spit]]
    [planck.io :refer [file-attributes]]
    [planck.shell :refer [sh]]
    [sitegen.urls :as urls]
    [sitegen.layout :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]))

;;---------------------------------------------------------------
;; API Retrieval
;;---------------------------------------------------------------

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

;;---------------------------------------------------------------
;; Page Rendering
;;---------------------------------------------------------------

(defn create-sym-page! [{:keys [name name-encode ns]}]
  (->> ""
       (common-layout)
       (render-html)
       (urls/write! (urls/api-symbol ns name-encode))))

(defn render! []
  (doseq [ns (keys (:namespaces api))]
    (urls/make-dir! (urls/api-ns ns)))
  (doseq [sym (vals (:symbols api))]
    (create-sym-page! sym)))
