(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [planck.core :refer [slurp spit]]
    [planck.io :refer [file-attributes]]
    [planck.shell :refer [sh]]
    [sitegen.urls :as urls]
    [sitegen.layout :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]
    [markdown.core :refer [md->html]]))

;;---------------------------------------------------------------
;; API Retrieval
;;---------------------------------------------------------------

(def api nil)

(defn slurp-url [url]
  (:out (sh "curl" url)))

(defn slurp-url-json [url]
  (-> (slurp-url url)
      (js/JSON.parse)
      (js->clj :keywordize-keys true)))

(def versions-url "https://api.github.com/repos/cljsinfo/cljs-api-docs/tags")
(defn api-url [version] (str "https://raw.githubusercontent.com/cljsinfo/cljs-api-docs/" version "/cljs-api.edn"))
(defn api-filename [version] (str "cljs-api-" version ".edn"))

; keys here: https://developer.github.com/v3/repos/#list-tags
(defn get-latest-version []
  (println "Checking latest version...")
  (->> (slurp-url-json versions-url)
       (first)
       (:name)))

(defn set-from-download!
  [filename]
  (->> (slurp filename)
       (read-string)
       (set! api)))

(defn update! []
  (let [version (get-latest-version)
        filename (api-filename version)
        downloaded? (boolean (file-attributes filename))]
    (when-not downloaded?
      (println (str "Downloading latest API " version "..."))
      (->> (api-url version)
           (slurp-url)
           (spit filename)))
    (set-from-download! filename)))

;;---------------------------------------------------------------
;; Page Rendering
;;---------------------------------------------------------------

(defn history-string [history]
  (let [change-str {"-" "Removed in "
                    "+" "Added in "}]
    (string/join ", "
      (for [[change version] history]
        (str (change-str change) version)))))

(defn signature-string [name args-str]
  (let [args (second (re-find #"^\[(.*)\]$" args-str))
        all-args (string/join " " [name args])]
    (str "(" all-args ")")))

(defn syntax-sym-page [sym])

(defn cljsdoc-url [sym]
  (str "https://github.com/cljsinfo/cljs-api-docs/blob/master/cljsdoc/"
       (:full-name-encode sym)
       ".cljsdoc"))

(defn api-sym-page [sym]
  [:div
    [:h1 (:full-name sym)]
    (when-let [name (:known-as sym)]
      [:em "known as " name])
    (when-let [full-name (:moved sym)]
      [:em [:strong "MOVED"] ", please see " full-name])
    [:table
      [:tr
        [:td (:type sym)]
        [:td (history-string (:history sym))]
        (when-let [clj-fullname (:clj-symbol sym)]
          [:td
            (when (= "clojure" (-> sym :source :repo))
              "imported ")
            clj-fullname])]]
    (when-let [signature (seq (:signature sym))]
      [:ul
        (for [args-str signature]
          [:li [:code (signature-string (:name sym) args-str)]])])
    [:hr]
    (when-let [md-desc (:description sym)]
      (list
        [:div (md->html md-desc)]
        [:hr]))
    (when-let [examples (seq (:examples sym))]
       (list
         [:h3 "Examples:"]
         (for [example examples]
           (list
             [:div (md->html (:content example))]
             [:hr]))))
    (when-let [related (seq (:related sym))]
      (list
        [:h3 "See Also:"]
        [:ul
          (for [full-name related]
            (let [{:keys [ns name-encode]} (get-in api [:symbols full-name])
                  url (urls/pretty (urls/api-symbol ns name-encode))]
              [:li [:a {:href url} full-name]]))]
        [:hr]))
    (when-let [docstring (:docstring sym)]
      (list
        [:h3 "Source docstring:"]
        [:pre docstring]))
    (when-let [source (:source sym)]
      (list
        [:h3 (:title source)]
        [:pre [:code (:code source)]]
        [:hr]))
    (when-let [extra-sources (seq (:extra-sources sym))]
      (for [source extra-sources]
        (list
          [:h3 (:title source)]
          [:pre [:code (:code source)]]
          [:hr])))
    [:div
      [:a {:href (cljsdoc-url sym)} "Edit Here!"]]])

(defn create-sym-page! [{:keys [ns name-encode] :as sym}]
  (let [content (if (= ns "syntax")
                  (syntax-sym-page sym)
                  (api-sym-page sym))]
    (->> content
         (common-layout)
         (render-html)
         (urls/write! (urls/api-symbol ns name-encode)))))

;; Set this to a symbol name to render a symbol page for it and nothing else.
;; This speeds up development.
(def render-one-sym nil)

(defn render! []
  (doseq [ns (keys (:namespaces api))]
    (urls/make-dir! (urls/api-ns ns)))
  (let [syms (if render-one-sym
                [(get-in api [:symbols render-one-sym])]
                (vals (:symbols api)))]
    (doseq [sym syms]
      (create-sym-page! sym))))
