(ns sitegen.api-pages
  (:require
    [clojure.string :as string]
    [util.io :as io]
    [util.markdown :as markdown]
    [util.console :as console]
    [util.highlight :refer [highlight-code]]
    [util.hiccup :as hiccup]
    [sitegen.urls :as urls]
    [sitegen.layout :refer [common-layout sidebar-layout]]
    [sitegen.api :refer [api
                         version
                         version-has-news-post?
                         hide-lib-ns?
                         lib-namespaces
                         compiler-namespaces
                         sym-removed?
                         get-ns-symbols
                         type-or-protocol?
                         docname-url
                         docname-display]]))

;;---------------------------------------------------------------
;; Sidebar Rendering
;;---------------------------------------------------------------

(defn overview-sidebar []
  [:div
    [:div
     version " | "
     [:a {:href (urls/pretty urls/versions)} "Versions"]]
    [:div.sep]
    [:div [:a {:href (urls/pretty (urls/api-ns "syntax"))} (get-in api [:namespaces "syntax" :display])]]
    [:div [:a {:href (urls/pretty (urls/api-ns "special"))} (get-in api [:namespaces "special" :display])]]
    [:div.sep]
    [:div "Namespaces"]
    (for [ns- (lib-namespaces)]
      [:div [:a {:href (urls/pretty (urls/api-ns ns-))} ns-]])
    [:div.sep]
    [:div "Compiler"]
    (for [ns- (compiler-namespaces)]
      [:div [:a {:href (urls/pretty (urls/api-compiler-ns ns-))} ns-]])])

(defn ns-sidebar [api-type ns-]
  (let [title (or (get-in api [:namespaces ns- :display]) ns-)
        syms (get-ns-symbols api-type ns-)
        main-syms (remove type-or-protocol? syms)
        type-syms (filter type-or-protocol? syms)]
    [:div
      [:a {:href (urls/pretty urls/api-index)} "< Back to Overview"]
      [:div.sep]
      (for [sym main-syms]
        (let [name- (or (:display sym) (:name sym))]
          [:div [:a {:href (str "#" (:name-encode sym))} name-]]))
      (when (seq type-syms)
        (list
          [:div.sep]
          [:div "Types and Protocols"]
          (for [sym type-syms]
            (let [parent-type (:parent-type sym)
                  name- (if parent-type
                          (subs (:name sym) (inc (count parent-type)))
                          (or (:display sym) (:name sym)))]
              [:div
                [:span {:style "opacity: 0.3"} (when parent-type "└")]
                [:a {:href (str "#" (:name-encode sym))} name-]]))))]))

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

(defn markdown-with-doc-biblio
  [body biblio]
  (markdown/render
    (string/join "\n"
      (cons
        body
        (for [docname biblio]
          (str "[doc:" docname "]:" (docname-url docname)))))))

(defn sym-page [sym]
  [:div
    [:h1 (docname-display (:full-name sym))]
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
      (for [u usage]
        [:div [:code u]]))
    [:hr]
    (when-let [details (:details sym)]
      (list
        [:div (markdown-with-doc-biblio details (:md-biblio sym))]
        [:hr]))
    (when-let [examples (:examples sym)]
       (list
         [:h3 "Examples:"]
         [:div (markdown-with-doc-biblio examples (:md-biblio sym))]
         [:hr]))
    (when-let [related (seq (:related sym))]
      (list
        [:h3 "See Also:"]
        [:ul
          (for [docname related]
            [:li [:a {:href (docname-url docname)}
                   (docname-display docname)]])]
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

(defn sym-overview [sym]
  [:div {:style "position: relative;"}
    (let [id (:name-encode sym)
          title (or (:display sym) (:name sym))]
      [:div {:id id}
        [:strong title]
        (when-let [name (:known-as sym)]
          [:em "- known as " name])
        " - " (:type sym)])
    [:div {:style "position: absolute; right: 0; top: 0;"}
      [:a {:href (urls/pretty (urls/api-symbol (:ns sym) (:name-encode sym)))} "more details >"]]
    (when-let [usage (seq (:usage sym))]
      [:div.sep
        (for [u usage]
          [:div [:code u]])])
    (when-let [docstring (:docstring sym)]
      [:div.sep docstring])
    [:div.sep]
    (interpose " | "
      (for [source (cons (:source sym) (:extra-sources sym))
            :when source]
        [:a {:href (:url source)} (:title source)]))])

(defn ns-page [api-type ns-]
  (sidebar-layout
    (ns-sidebar api-type ns-)
    (let [ns-data (get-in api [:namespaces ns-])
          title (or (:display ns-data) ns-)
          syms (get-ns-symbols api-type ns-)
          main-syms (remove type-or-protocol? syms)
          type-syms (filter type-or-protocol? syms)]
      [:div
        [:h2 title]
        (when-not (get #{"syntax" "special" "cljs.core"} ns-)
          [:div (history-string (:history ns-data))])
        [:div.sep]
        (when-let [details (:details ns-data)]
          [:div (markdown-with-doc-biblio details (:md-biblio ns-data))])
        [:hr]
        (interpose [:hr]
          (for [sym main-syms]
            (sym-overview sym)))
        (when (seq type-syms)
          (list
            [:div.sep]
            [:h4 "Types and Protocols"]
            [:hr]
            (interpose [:hr]
              (for [sym type-syms]
                (sym-overview sym)))))])))

(defn ns-overview [api-type ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        ns-url (if (= api-type :compiler) urls/api-compiler-ns urls/api-ns)
        title (or (:display ns-data) ns-)
        syms (get-ns-symbols api-type ns-)
        main-syms (remove type-or-protocol? syms)
        type-syms (filter type-or-protocol? syms)]
    (list
      [:h4 [:a {:href (urls/pretty (ns-url ns-))} title]]
      [:p (:summary ns-data)]
      (for [sym-data main-syms]
        (let [name- (or (:display sym-data) (:name sym-data))]
          [:span [:a {:href (urls/pretty (urls/api-symbol ns- (:name-encode sym-data)))} name-] " "]))
      (when (seq type-syms)
        (list
          [:div.sep]
          [:span "Types and Protocols: "]
          (for [sym-data type-syms]
            (let [name- (or (:display sym-data) (:name sym-data))]
              [:span [:a {:href (urls/pretty (urls/api-symbol ns- (:name-encode sym-data)))} name-] " "])))))))

(defn index-page []
  (sidebar-layout
    (overview-sidebar)
    [:div
      [:h2 "API Documentation"]
      [:p
        "Welcome! This is a comprehensive reference for ClojureScript's"
        " syntax, standard library, and compiler API. "
        [:strong
         [:a {:href "http://cljs.info/cheatsheet" :target "_blank"}
          "See the Cheatsheet for quick reference"]]]
      [:p
        "Documentation is versioned and supplemented by curated descriptions,"
        "examples, and cross-refs.  Community contributions welcome."]
      [:p [:strong "Current Version:"] " " version]
      [:hr]
      (ns-overview :syntax "syntax")
      [:hr]
      (ns-overview :library "special")
      [:hr]
      [:h2 "Namespaces"]
      (interpose [:hr]
        (for [ns- (lib-namespaces)]
          (ns-overview :library ns-)))
      [:hr]
      [:h2 "Compiler"]
      (interpose [:hr]
        (for [ns- (compiler-namespaces)]
          (ns-overview :compiler ns-)))]))

(defn create-sym-page! [{:keys [ns name-encode] :as sym}]
  (->> (sym-page sym)
       (common-layout)
       (hiccup/render)
       (urls/write! (urls/api-symbol ns name-encode))))

(defn create-ns-page! [api-type ns-]
  (let [filename (urls/api-ns* api-type ns-)]
    (urls/make-dir! filename)
    (->> (ns-page api-type ns-)
         (common-layout)
         (hiccup/render)
         (urls/write! filename))))

(defn create-index-page! []
  (->> (index-page)
       (common-layout)
       (hiccup/render)
       (urls/write! urls/api-index)))

(defn render! []
  (doseq [ns (keys (:namespaces api))]
    (urls/make-dir! (urls/api-ns ns)))

  (doseq [api-type [:syntax :library :compiler]]
    (doseq [ns- (get-in api [:api api-type :namespace-names])]
      (create-ns-page! api-type ns-)))

  (let [syms (->> (vals (:symbols api))
                  (sort-by :full-name))]
    (create-index-page!)
    (doseq [sym syms]
      (console/replace-line "Creating page for" (:full-name sym))
      (create-sym-page! sym))
    (console/replace-line "Done creating API pages."))
  (println))
