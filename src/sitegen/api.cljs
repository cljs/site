(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [util.io :as io]
    [sitegen.urls :as urls]))

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

(defn pre-releases
  "List pre-releases that came before a main release."
  [version]
  (let [all-versions (get-in api [:history :versions])
        priors (loop [[v & rest] all-versions]
                 (if (= v version) rest (recur rest)))]
    (take-while #(not (version-has-news-post? %)) priors)))

;;---------------------------------------------------------------
;; Namespace Utilities
;;---------------------------------------------------------------

(defn hide-lib-ns? [ns-]
  (let [ns-data (get-in api [:namespaces ns-])]
    (or
      ;; pseudo-namespaces our handled manually (syntax, special, specialrepl)
      (:pseudo-ns? ns-data) ;; we handle these manually

      ;; clojure.browser is basically deprecated, so we don't show it.
      ;; https://groups.google.com/d/msg/clojurescript/OqkjlpqKSQY/9wVGC5wFjcAJ
      (string/starts-with? ns- "clojure.browser"))))

(defn lib-namespaces []
  (->> api :api :library :namespace-names
       (filter (comp not hide-lib-ns?))
       (sort)))

(defn compiler-namespaces []
  (->> api :api :compiler :namespace-names
       (sort)))

(defn sym-removed? [sym-data]
  (= "-" (first (last (:history sym-data)))))

(defn get-ns-symbols [api-type ns-]
  (let [syms
        (->> (get-in api [:api api-type :symbol-names])
             (filter #(string/starts-with? % (str ns- "/")))
             (map #(get-in api [:symbols %]))
             (remove sym-removed?))]
    (if (= "syntax" ns-)
      (sort-by #(string/replace (string/upper-case (:name %)) #"[^a-zA-Z ]" "") syms)
      (sort-by :name syms))))

(defn type-or-protocol? [sym-data]
  (or (get #{"type" "protocol"} (:type sym-data))
      (:parent-type sym-data)))

(defn fullname->url [full-name]
  (let [{:keys [ns name-encode]} (get-in api [:symbols full-name])]
    (urls/pretty (urls/api-symbol ns name-encode))))
