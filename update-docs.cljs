#!/usr/bin/env planck
(ns site.update-docs
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :refer [join]]
    [planck.core :refer [slurp spit]]
    [planck.shell :refer [sh]]))

(def root "site-jekyll/docs")

(defn get-api-data!
  "Get the latest api docs data structure for ClojureScript."
  []
  (let [url "https://raw.githubusercontent.com/cljsinfo/cljs-api-docs/catalog/cljs-api.edn"
        filename "cljs-api.edn"]
    (println "Retrieving" url "...")
    (sh "rm" "-f" filename)
    (sh "wget" url)
    (read-string (slurp filename))))

(defn get-namespaces
  "Get the namespace names from the api data."
  [api]
  (->> (:symbols api)
       (vals)
       (map :ns)
       (set)))

(defn create-namespace-dirs!
  "Create directories to hold the symbol files for each namespace."
  [api]
  (println "Creating namespace directories...")
  (let [ns->dir #(str root "/" %)
        dirs (->> (get-namespaces api)
                  (map ns->dir))]
    (apply sh "mkdir" "-p" dirs)))

(defn sym-file-content
  "Generate the file content for the given symbol."
  [sym]
  ;; TODO: fill out
  (:full-name sym))

(defn create-symbol-files!
  "Generate all the symbol files."
  [api]
  (println "Creating symbol files...")
  (doseq [sym (vals (:symbols api))]
    (let [filename (str root "/" (:full-name-encode sym) ".md")]
      (spit filename (sym-file-content sym)))))

(defn main []
  (let [api (get-api-data!)]
    (create-namespace-dirs! api)
    (create-symbol-files! api)))

(main)
