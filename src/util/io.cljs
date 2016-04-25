(ns util.io
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :refer [starts-with?]]))

;; Node Implementation

(def fs (js/require "fs"))
(def fs-extra (js/require "fs-extra"))
(def request (js/require "sync-request"))
(def existsSync (js/require "exists-sync"))

(def request-opts
  #js{:headers
       #js{:user-agent "cljsinfo/site"}})

(defn url? [path]
  (or (starts-with? path "http://")
      (starts-with? path "https://")))

(defn slurp [path]
  (if (url? path)
    (.toString (.getBody (request "GET" path request-opts)))
    (.toString (.readFileSync fs path))))

(defn spit [path text]
  (.writeFileSync fs path text))

(defn mkdirs [path]
  (.mkdirsSync fs-extra path))

(defn path-exists? [path]
  (existsSync path))

;; Helpers

(defn slurp-json [path]
  (-> (slurp path)
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(defn slurp-edn [path]
  (-> (slurp path)
      (read-string)))
