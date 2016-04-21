(ns sitegen.io
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :refer [starts-with?]]))

;; Node Implementation

(def fs (js/require "fs"))
(def fs-extra (js/require "fs-extra"))
(def request (js/require "sync-request"))
(def existsSync (js/require "exists-sync"))

(defn url? [path]
  (or (starts-with? "http://")
      (starts-with? "https://")))

(defn slurp [path]
  (if (url? path)
    (.getBody (request "GET" url))
    (.readFileSync fs path)))

(defn spit [path text]
  (.writeFileSync fs path text))

(defn mkdirs [path]
  (.mkdirsSync fs-extra path))

(defn exists? [path]
  (existsSync path))

;; Helpers

(defn slurp-json [path]
  (-> (slurp path)
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(defn slurp-edn [path]
  (-> (slurp path)
      (read-string)))
