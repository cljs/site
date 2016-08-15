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
;; Syntax Categories
;;---------------------------------------------------------------

(def syntax-categories
  [{:title "Literals"
    :entries ["syntax/number"
              "syntax/string"
              "syntax/regex"
              "syntax/character"
              "syntax/keyword"
              "syntax/keyword-qualify"
              "syntax/symbol"]}
   {:title "Special Symbols"
    :entries ["syntax/true"
              "syntax/false"
              "syntax/nil"
              "syntax/NaN"
              "syntax/Infinity"]}
   {:title "Collections"
    :entries ["syntax/vector"
              "syntax/list"
              "syntax/map"
              "syntax/set"
              "syntax/ns-map"
              "syntax/ns-map-alias"]}
   {:title "Commenting"
    :entries ["syntax/comment"
              "syntax/shebang"
              "syntax/ignore"]}
   {:title "Function"
    :entries ["syntax/function"
              "syntax/arg"
              "syntax/rest"]}
   {:title "Destructuring"
    :entries ["syntax/destructure-vector"
              "syntax/destructure-map"]}
   {:title "Conventions"
    :entries ["syntax/predicate"
              "syntax/impure"
              "syntax/earmuffs"
              "syntax/unused"
              "syntax/whitespace"
              "syntax/comma"]}
   {:title "Symbol Resolution"
    :entries ["syntax/dot"
              "syntax/namespace"
              "syntax/js-namespace"
              "syntax/Math-namespace"]}
   {:title "Tagged Literals"
    :entries ["syntax/tagged-literal"
              "syntax/js-literal"
              "syntax/inst-literal"
              "syntax/uuid-literal"
              "syntax/queue-literal"]}
   {:title "Quoting"
    :entries ["syntax/quote"
              "syntax/syntax-quote"
              "syntax/unquote"
              "syntax/unquote-splicing"
              "syntax/auto-gensym"]}
   {:title "Vars"
    :entries ["syntax/meta"
              "syntax/var"
              "syntax/deref"]}
   {:title "Reader Conditionals"
    :entries ["syntax/cond"
              "syntax/cond-splicing"]}
   {:title "Misc"
    :entries ["syntax/dispatch"
              "syntax/unreadable"
              "syntax/eval"]}])

(defn ensure-all-syntax-categorized []
  (let [all (->> (:symbols api)
                 (vals)
                 (filter #(= (:ns %) "syntax"))
                 (map :full-name)
                 (set))
        categorized (->> syntax-categories
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
  (let [ns-data (get-in api [:namespaces ns-])]
    (or
      ;; pseudo-namespaces our handled manually (syntax)
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

;;---------------------------------------------------------------
;; Docname Utilities
;;---------------------------------------------------------------

(defn parse-docname
  "foo/bar      <-- normal symbol
   foo          <-- namespace `foo`
   compiler/foo <-- compiler namespace `foo`"
  [docname]
  (let [[a b] ((juxt namespace name) (symbol docname))]
    (cond
      (= a "compiler") {:compiler? true, :ns a}
      (nil? a)         {:ns b}
      :else            {:ns a, :name b})))

(defn docname-url
  [docname]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (urls/pretty
      (if name
        (urls/api-sym-prev ns (get-in api [:symbols docname :name-encode]))
        (if compiler?
          (urls/api-compiler-ns ns)
          (urls/api-ns ns))))))

(defn docname-display
  [docname]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (if name
      (let [{:keys [repl-only? display-as]} (get-in api [:symbols docname])]
        (cond
          repl-only? (str name " (repl)")
          (= ns "cljs.core") name
          (= ns "syntax") display-as
          :else docname))
      (if compiler?
        (str ns " (compiler)")
        (or (get-in api [:namespaces ns :display-as]) ns)))))


(defn check! []
  (ensure-all-syntax-categorized))
