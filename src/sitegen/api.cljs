(ns sitegen.api
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [sitegen.io :as io]
    [sitegen.urls :as urls]
    [sitegen.layout :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]
    [sitegen.markdown :refer [md->html highlight-code]]
    [sitegen.console :as console]))

;;---------------------------------------------------------------
;; API Retrieval
;;---------------------------------------------------------------

(def api nil)

(def versions-url "https://api.github.com/repos/cljsinfo/cljs-api-docs/tags")
(defn api-url [version] (str "https://raw.githubusercontent.com/cljsinfo/cljs-api-docs/" version "/cljs-api.edn"))
(defn api-filename [version] (str "cljs-api-" version ".edn"))

; keys here: https://developer.github.com/v3/repos/#list-tags
(defn get-latest-version []
  (println "Checking latest version...")
  (->> (io/slurp-json versions-url)
       (first)
       (:name)))

(defn set-from-download!
  [filename]
  (->> (io/slurp filename)
       (read-string)
       (set! api)))

(defn update! []
  (let [version (get-latest-version)
        filename (api-filename version)
        downloaded? (io/path-exists? filename)]
    (when-not downloaded?
      (println (str "Downloading latest API " version "..."))
      (->> (api-url version)
           (io/slurp)
           (io/spit filename)))
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

(defn sym-source
  [{:keys [title code repo filename] :as source}]
  (list
    [:div
      (:title source) " @ "
      [:a {:href (:url source)}
        (str repo ":" filename)]]
    [:pre [:code (highlight-code (:code source) "clj")]]
    [:hr]))

(defn api-sym-page [sym]
  [:div
    [:h1 (or (:display sym) (:full-name sym))]
    (when-let [name (:known-as sym)]
      [:em "known as " name])
    (when-let [full-name (:moved sym)]
      [:em [:strong "MOVED"] ", please see " full-name])
    [:table
      [:tr
        [:td (:type sym)]
        [:td (history-string (:history sym))]
        (when-let [{:keys [full-name url]} (:clj-equiv sym)]
          [:td
            (when (= "clojure" (-> sym :source :repo))
              "imported ")
            [:a {:href url}
              [:img {:src "/img/clojure-icon.gif"
                     :height "24px"
                     :valign "middle"}]
              " " full-name]])
        (when-let [{:keys [clj-url edn-url]} (:syntax-equiv sym)]
          (list
            (when clj-url
              [:td
                [:a {:href clj-url}
                  [:img {:src "/img/clojure-icon.gif"
                         :height "24px"
                         :valign "middle"}]
                  " in clojure"]])
            (when edn-url
              [:td [:a {:href edn-url} " in edn"]])))]]

    (when-let [usage (seq (:usage sym))]
      [:ul
        (for [u usage]
          [:li [:code u]])])
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
        "Source docstring:"
        [:pre [:code docstring]]))
    (when-let [source (:source sym)]
      (sym-source source))
    (when-let [extra-sources (seq (:extra-sources sym))]
      (for [source extra-sources]
        (sym-source source)))

    [:div
      [:a {:href (:cljsdoc-url sym)} "Edit Here!"]]])

(defn create-sym-page! [{:keys [ns name-encode] :as sym}]
  (->> (api-sym-page sym)
       (common-layout)
       (render-html)
       (urls/write! (urls/api-symbol ns name-encode))))

;; Set this to a symbol name to render a symbol page for it and nothing else.
;; This speeds up development.
(def render-one-sym nil)

(defn render! []
  (doseq [ns (keys (:namespaces api))]
    (urls/make-dir! (urls/api-ns ns)))
  (let [syms (if render-one-sym
                [(get-in api [:symbols render-one-sym])]
                (vals (:symbols api)))]
    (doseq [sym (sort-by :full-name syms)]
      (console/replace-line "Creating page for" (:full-name sym))
      (create-sym-page! sym))
    (console/replace-line "Done creating docs pages."))
  (println))
