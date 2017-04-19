(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [clojure.set :as set]
    [util.io :as io]
    [sitegen.urls :as urls]))

;;---------------------------------------------------------------
;; API Retrieval
;;---------------------------------------------------------------

(def api nil)
(def version nil)
(def version-has-news-post? nil)

(defn master-version? [v]
  (string/ends-with? v "+"))

(def api-url "https://raw.githubusercontent.com/cljs/api/master/cljs-api.edn")
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
;; API Categories
;;---------------------------------------------------------------

(defn get-ns-categories [ns]
  (let [categories (read-string (io/slurp (str "api-categories/" ns ".edn")))
        process-entry #(str ns "/" %)
        process-entries #(mapv process-entry %)
        process-category #(update % :entries process-entries)]
    (mapv process-category categories)))

(def categories
  {:syntax (get-ns-categories "syntax")
   :cljs.core (get-ns-categories "cljs.core")})

(defn ensure-all-syntax-categorized []
  (let [all (->> (:symbols api)
                 (vals)
                 (filter #(= (:ns %) "syntax"))
                 (map :full-name)
                 (set))
        categorized (->> (:syntax categories)
                         (map :entries)
                         (apply concat)
                         (set))
        unrecognized (set/difference categorized all)
        uncategorized (set/difference all categorized)]
    (when (or (seq uncategorized)
              (seq unrecognized))
      (when (seq uncategorized)
        (println "The following syntax entries are not categorized:")
        (doseq [s uncategorized]
          (println "  -" s)))
      (when (seq unrecognized)
        (println "The following syntax entries are not recognized:")
        (doseq [s unrecognized]
          (println "  -" s)))
      (js/process.exit 1))))

;;---------------------------------------------------------------
;; Namespace Utilities
;;---------------------------------------------------------------

(defn hide-lib-ns? [ns-]
  ;; clojure.browser is basically deprecated, so we don't show it.
  ;; https://groups.google.com/d/msg/clojurescript/OqkjlpqKSQY/9wVGC5wFjcAJ
  (string/starts-with? ns- "clojure.browser"))

(defn lib-namespaces []
  (->> (get-in api [:api :library :namespace-names])
       (filter (comp not hide-lib-ns?))
       (sort)))

(defn compiler-namespaces []
  (->> (get-in api [:api :compiler :namespace-names])
       (sort)))

(defn options-namespaces []
  (->> (get-in api [:api :options :namespace-names])
       (remove #(get-in api [:namespaces % :sub-options-sym]))
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

;;---------------------------------------------------------------
;; Docname Utilities
;;---------------------------------------------------------------

(defn split-symbol [sym]
  (when sym
    ((juxt namespace name) (symbol sym))))

(defn parse-docname
  "foo/bar      <-- normal symbol
   foo          <-- namespace `foo`
   compiler/foo <-- compiler namespace `foo`"
  [docname]
  (let [[a b] (split-symbol docname)]
    (cond
      (= a "compiler") {:compiler? true, :ns a}
      (nil? a)         {:ns b}
      :else            {:ns a, :name b})))

(defn guess-api-type
  "cljs.core/foo can either be from the library and/or compiler API.  In some contexts,
   we cannot determine which, so we favor the library API."
  [full-name]
  (first (filter #(get-in api [:api % :symbol-names full-name]) [:library :compiler :syntax :options])))

(defn docname-url
  [docname & {:keys [preview?]}]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (urls/pretty
      (if name
        (if-let [sub-opt (get-in api [:symbols (get-in api [:namespaces ns :sub-options-sym])])]
          (str (urls/api-sym (:ns sub-opt) (:name-encode sub-opt)) "#" name)
          (if preview?
            (urls/api-sym-prev (guess-api-type docname) ns (get-in api [:symbols docname :name-encode]))
            (urls/api-sym ns (get-in api [:symbols docname :name-encode]))))
        (if compiler?
          (urls/api-compiler-ns ns)
          (urls/api-ns ns))))))

(defn docname-display
  [docname & {:keys [prefer-docname?]}]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (if name
      (let [{:keys [repl-only? display-as]} (get-in api [:symbols docname])]
        (cond
          repl-only? (str name " (repl)")
          (= ns "cljs.core") name
          (= ns "syntax") display-as
          prefer-docname? docname
          (= ns "compiler-options") display-as
          (= ns "repl-options") display-as
          (= ns "warnings") display-as
          (= ns "closure-warnings") display-as
          :else docname))
      (if compiler?
        (str ns " (compiler)")
        (or (get-in api [:namespaces ns :display-as]) ns)))))


(defn check! []
  (ensure-all-syntax-categorized))
