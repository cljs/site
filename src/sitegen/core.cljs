(ns sitegen.core
  (:require
    [planck.shell :refer [sh]]
    [planck.core :refer [spit]]
    [sitegen.fetch-api :refer [get-latest-api!]]
    [sitegen.pages.index :as index]
    [hiccups.runtime :refer [render-html]]))

(def root-docs "FIXME")

(defn create-namespace-dirs!
  "Create directories to hold the symbol files for each namespace."
  [api]
  (let [nss (keys (:namespaces api))]
    (println (str "Creating namespace directories (" (count nss) ") at " root-docs "/* ..."))
    (let [ns->dir #(str root-docs "/" %)
          dirs (map ns->dir nss)]
      (apply sh "mkdir" "-p" dirs))))

(defn gen! []
  (spit "index.html" (render-html (index/page))))

(defn -main []
  (get-latest-api!)
  (gen!))
