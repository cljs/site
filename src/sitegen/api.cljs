(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [util.io :as io]))

;;---------------------------------------------------------------
;; API Retrieval
;;---------------------------------------------------------------

(def api nil)
(def version nil)
(def version-has-news-post? nil)

(def api-url "https://raw.githubusercontent.com/cljsinfo/cljs-api-docs/master/cljs-api.edn")
(def api-filename "cljs-api.edn")

(defn update! []
  (let [downloaded? (io/path-exists? api-filename)]
    (when-not downloaded?
      (println "Downloading latest API...")
      (->> (io/slurp api-url)
           (io/spit api-filename)))
    (set! api (read-string (io/slurp api-filename)))
    (set! version (:version api))))
