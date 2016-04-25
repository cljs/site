(ns sitegen.devserver
  (:require
    [util.io :refer [path-exists?]]))

(def express (js/require "express"))
(def fs (js/require "fs"))
(def path (js/require "path"))

(def root (str (.resolve path ".") "/output"))
(def port 3795)

(defn on-req
  "Map URL `.../page` to `.../page.html` if it exists."
  [req res next]
  (when (path-exists? (str root (str (.-path req) ".html")))
    (set! (.-url req) (str (.-url req) ".html")))
  (next))

(defn on-init []
  (println "server listening on port" port))

(defn -main []
  (let [server (express)]
    (.use server on-req)
    (.use server (.static express root))
    (.listen server port on-init)))

(set! *main-cli-fn* -main)
(enable-console-print!)
