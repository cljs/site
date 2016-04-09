(ns sitegen.core
  (:require
    [planck.shell :refer [sh]]
    [sitegen.fetch-api :refer [get-latest-api!]]))

(def root-docs "FIXME")

(defn create-namespace-dirs!
  "Create directories to hold the symbol files for each namespace."
  [api]
  (let [nss (keys (:namespaces api))]
    (println (str "Creating namespace directories (" (count nss) ") at " root-docs "/* ..."))
    (let [ns->dir #(str root-docs "/" %)
          dirs (map ns->dir nss)]
      (apply sh "mkdir" "-p" dirs))))

(defn -main []
  (get-latest-api!))
