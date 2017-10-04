(ns sitegen.docset
  (:require-macros
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<! chan close!]]
    [util.hiccup :as hiccup]
    [util.io :refer [delete mkdirs copy]]
    [sitegen.api :as api :refer [api]]
    [sitegen.urls :as urls :refer [*root*]]
    [sitegen.api-pages :as api-pages]
    [sitegen.layout :as layout]
    [sitegen.api :refer [docname-display]]
    [sitegen.versions :refer [versions-page]]
    [sitegen.news :as news]
    [sitegen.state :refer [*docset?*]]))

(def sqlite3 (js/require "sqlite3"))
(def child-process (js/require "child_process"))
(def spawn-sync (.-spawnSync child-process))


;; code derived from Lokeshwaran's (@dlokesh) project:
;; https://github.com/dlokesh/clojuredocs-docset

(def docset-name "ClojureScript.docset")
(def tar-name "ClojureScript.tgz")

(def work-dir "docset")

(def html-path (str work-dir "/html"))
(def docset-path (str work-dir "/" docset-name))
(def tar-path (str work-dir "/" tar-name))

(def docset-docs-path (str docset-path "/Contents/Resources/Documents"))
(def db-path (str docset-path "/Contents/Resources/docSet.dsidx"))

(def type->dash
  {"var"                 "Variable"
   "dynamic var"         "Variable"
   "protocol"            "Protocol"
   "type"                "Type"
   "macro"               "Macro"
   "function"            "Function"
   "function/macro"      "Function"
   "special form"        "Statement" ;; <-- Pending "Special Form"
   "special form (repl)" "Statement" ;;     (avaiable in next Dash version)
   "tagged literal"      "Tag"
   "syntax"              "Operator"  ;; <-- Pending "Syntax"
   "special symbol"      "Constant"
   "symbolic value"      "Constant"
   "special namespace"   "Namespace"
   "binding"             "Builtin"
   "special character"   "Builtin"
   "convention"          "Style"
   "multimethod"         "Method"
   "option"              "Option"
   "warning"             "Error"})

(defn docset-entries []
  (binding [urls/*case-sensitive* false]
    (doall
      (concat
        ;; Sections
        [{:$name "Overview" :$type "Section" :$path urls/api-index}
         {:$name "Versions" :$type "Section" :$path urls/versions}]

        ;; News Posts
        (for [post news/posts]
          {:$name (str "Release " (:version post))
           :$type "Event"
           :$path (:url post)})

        ;; Namespaces
        (for [api-type [:syntax :library :compiler]
              ns- (get-in api [:api api-type :namespace-names])]
          {:$name (or (get-in api [:namespaces ns- :display-as]) ns-)
           :$type "Namespace"
           :$path (urls/api-ns* api-type ns-)})

        ;; Symbols
        (for [sym (vals (:symbols api))]
          {:$name (or (:display-as sym) (:name sym))
           :$type (type->dash (:type sym))
           :$path (urls/api-sym (:ns sym) (:name-encode sym))})))))

;;-----------------------------------------------------------------------------
;; Database (Dash index)
;;-----------------------------------------------------------------------------

(defn sqlite-error [sql params error]
  (println "SQL error occured:")
  (println "  query:" sql)
  (println "  params:" params)
  (println "  error:" error)
  (js/process.exit 1))

(defn run-db
  ([db sql]
   (run-db db sql nil))
  ([db sql params]
   (let [done-chan (chan)
         params (when params (clj->js params))
         callback (fn [error]
                    (when error (sqlite-error sql params error))
                    (close! done-chan))]
     (if params
       (.run db sql params callback)
       (.run db sql callback))
     done-chan)))

(defn build-db! []
  (go
    (let [db (new sqlite3.Database db-path)]
      (<! (run-db db "DROP TABLE IF EXISTS searchIndex"))
      (<! (run-db db "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)"))
      (<! (run-db db "CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path)"))

      ;; Run insertions in parallel
      (doseq [q (mapv
                  #(run-db db "INSERT INTO searchIndex(name, type, path) VALUES ($name, $type, $path)" %)
                  (docset-entries))]
        (<! q))
      (.close db))))

;;-----------------------------------------------------------------------------
;; Docset pages
;;-----------------------------------------------------------------------------

(defn docset-layout [opts content]
  [:html
    (layout/head (merge {:css "/css/docset.css"} (:head opts)))
    [:body
      [:div.container
        content
        (layout/body-footer)]]])

(defn create-news-page! [post]
  (let [url (:url post)]
    (binding [*root* (urls/get-root url)]
      (->> (news/post-page-body post)
           (docset-layout nil)
           (hiccup/render)
           (urls/write! url)))))

(defn create-versions-page! []
  (let [url urls/versions]
    (binding [*root* (urls/get-root url)]
      (->> (versions-page)
           (docset-layout {:head {:title "CLJS Versions"}})
           (hiccup/render)
           (urls/write! url)))))

(defn create-sym-page! [{:keys [ns name-encode] :as sym}]
  (let [url (urls/api-sym ns name-encode)]
    (binding [*root* (urls/get-root url)]
      (->> (api-pages/sym-page sym)
           (docset-layout {:head {:title (str "CLJS - " (docname-display (:full-name sym)))}})
           (hiccup/render)
           (urls/write! url)))))

(defn create-ns-page! [api-type ns-]
  (let [url (urls/api-ns* api-type ns-)]
    (urls/make-dir! url)
    (binding [*root* (urls/get-root url)]
      (let [page (cond
                   (= ns- "syntax") (api-pages/syntax-ns-page)
                   (= api-type :options) (api-pages/options-ns-page ns-)
                   :else (api-pages/ns-page api-type ns-))]
        (->> page
             (docset-layout {:head {:title (str "CLJS - " ns-)}})
             (hiccup/render)
             (urls/write! url))))))

(defn create-index-page! []
  (let [url urls/api-index]
    (binding [*root* (urls/get-root url)]
      (->> (api-pages/index-page-body)
           (docset-layout {:head {:title "CLJS API"}})
           (hiccup/render)
           (urls/write! url)))))

(defn create-pages! []
  (binding [urls/*out-dir* docset-docs-path
            urls/*case-sensitive* false
            urls/*pretty-links* false
            *docset?* true]
    (doseq [ns (keys (:namespaces api))]
      (urls/make-dir! (urls/api-ns ns)))
    (urls/make-dir! urls/news-dir)

    (create-index-page!)
    (create-versions-page!)
    (doseq [post news/posts]
      (create-news-page! post))

    (doseq [api-type [:syntax :options :library :compiler]]
      (doseq [ns- (get-in api [:api api-type :namespace-names])]
        (create-ns-page! api-type ns-)))
    (doseq [sym (vals (:symbols api))]
      (create-sym-page! sym))))

;;-----------------------------------------------------------------------------
;; Main
;;-----------------------------------------------------------------------------

(defn create! []
  (go
    (println "Creating ClojureScript docset...")

    (println "Clearing previous docset folder...")
    (delete docset-path)
    (mkdirs docset-docs-path)

    (println "Generating docset pages...")
    (create-pages!)

    ;; copy over resources
    (copy "docset/icon.png" (str docset-path "/icon.png"))
    (copy "docset/Info.plist" (str docset-path "/Contents/Info.plist"))
    (copy "output/css" (str docset-docs-path "/css"))
    (copy "output/img" (str docset-docs-path "/img"))

    ;; reset/create tables
    (println "Creating index database...")
    (<! (build-db!))

    ;; create the tar file
    (println "Creating final docset tar file...")
    (spawn-sync "tar"
      #js["--exclude='.DS_Store'" "-czf" tar-name docset-name]
      #js{:cwd work-dir :stdio "inherit"})

    (println)
    (println "Created:" docset-path)
    (println "Created:" tar-path)))
