(ns sitegen.devserver
  (:require
    [sitegen.io :refer [path-exists?]]))

(def express (js/require "express"))
(def fs (js/require "fs"))

(def root (str js/__dirname "/output"))
(def port 3795)

(defn on-req
  "Map URL `.../page` to `.../page.html` if it exists."
  [req res next]
  (let [new-path (str (.-path req) ".html")]
    (when (path-exists? (str root new-path))
      (set! (.-path req) new-path)))
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
