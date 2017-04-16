(ns sitegen.api-pages
  (:require
    [clojure.string :as string]
    [util.io :as io]
    [util.markdown :as markdown]
    [util.console :as console]
    [util.highlight :refer [highlight-code]]
    [util.hiccup :as hiccup]
    [sitegen.urls :as urls :refer [*root*]]
    [sitegen.layout :refer [common-layout sidebar-layout]]
    [sitegen.state :refer [*docset?*]]
    [sitegen.api :refer [api
                         master-version?
                         version
                         version-has-news-post?
                         hide-lib-ns?
                         lib-namespaces
                         compiler-namespaces
                         options-namespaces
                         sym-removed?
                         get-ns-symbols
                         type-or-protocol?
                         docname-url
                         docname-display
                         categories]]))

(defn sym-doc-progress-color
  "Track documentation progress of a symbol by assigning it a color"
  [{:keys [summary details examples]}]
  (if (or summary details) (if examples "g" "y") "r"))

;;---------------------------------------------------------------
;; Sidebar Rendering
;;---------------------------------------------------------------

(defn index-sidebar []
  [:div
    [:div version]
    [:div.sep]
    [:div [:a {:href (str *root* (urls/pretty (urls/api-ns "syntax")))} (get-in api [:namespaces "syntax" :display-as])]]
    [:div.sep]
    (for [ns- (options-namespaces)]
      [:div [:a {:href (str *root* (urls/pretty (urls/api-ns ns-)))} (get-in api [:namespaces ns- :display-as])]])
    [:div.sep]
    [:div "Namespaces"]
    (for [ns- (lib-namespaces)]
      [:div [:a {:href (str *root* (urls/pretty (urls/api-ns ns-)))} ns-]])
    [:div.sep]
    [:div "Compiler"]
    (for [ns- (compiler-namespaces)]
      [:div [:a {:href (str *root* (urls/pretty (urls/api-compiler-ns ns-)))} ns-]])])

(defn ns-sidebar [api-type ns-]
  (let [title (or (get-in api [:namespaces ns- :display-as]) ns-)
        syms (get-ns-symbols api-type ns-)
        main-syms (remove type-or-protocol? syms)
        type-syms (filter type-or-protocol? syms)]
    [:div
      [:a {:href (str *root* (urls/pretty urls/api-index))} "< Back to Overview"]
      [:div.sep]
      (for [sym main-syms]
        (let [name- (or (:display-as sym) (:name sym))]
          [:div {:class (sym-doc-progress-color sym)}
            [:a {:href (str "#" (:name-encode sym))} name-]]))
      (when (seq type-syms)
        (list
          [:div.sep]
          [:div "Types and Protocols"]
          (for [sym type-syms]
            (let [parent-type (:parent-type sym)
                  name- (if parent-type
                          (subs (:name sym) (inc (count parent-type)))
                          (or (:display-as sym) (:name sym)))]
              [:div {:class (sym-doc-progress-color sym)}
                [:span {:style "opacity: 0.3"} (when parent-type "└")]
                [:a {:href (str "#" (:name-encode sym))} name-]]))))]))

(defn syntax-ns-sidebar []
  (let [ns- "syntax"
        title (get-in api [:namespaces ns- :display-as])]
    [:div
      [:a {:href (str *root* (urls/pretty urls/api-index))} "< Back to Overview"]
      (for [category (:syntax categories)]
        (list
          [:div.sep (:title category)]
          (for [full-name (:entries category)]
            (let [sym (get-in api [:symbols full-name])
                  name- (or (:display-as sym) (:name sym))]
              [:div {:class (sym-doc-progress-color sym)}
                [:a {:href (str "#" (:name-encode sym))} name-]]))))]))

(defn options-ns-sidebar [ns-]
  (let [title (get-in api [:namespaces ns- :display-as])
        syms (get-ns-symbols :options ns-)]
    [:div
      [:a {:href (str *root* (urls/pretty urls/api-index))} "< Back to Overview"]
      [:div.sep]
      (for [sym syms]
        (let [name- (:display-as sym)]
          [:div {:class (sym-doc-progress-color sym)}
            [:a {:href (str "#" (:name-encode sym))} name-]]))]))

;;---------------------------------------------------------------
;; Page Utils
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
  [body biblio & {:keys [preview?]}]
  (markdown/render
    (string/join "\n"
      (cons
        body
        (for [docname biblio]
          (str "[doc:" docname "]:" (str *root* (docname-url docname :preview? preview?))))))))

;;---------------------------------------------------------------
;; Warnings
;;---------------------------------------------------------------

(def warnings-option->pseudo-ns
  "All the specific warning types for an warning option are listed in a pseudo-ns."
  {"compiler-options/warnings"         "warnings"
   "compiler-options/closure-warnings" "closure-warnings"})

(defn warning-symbols
  "Get all the warning symbols for the given warning option."
  [option-sym]
  (let [ns- (warnings-option->pseudo-ns (:full-name option-sym))
        syms (get-ns-symbols :options ns-)]
    syms))

(defn warnings-list
  "Get a markdown list of warning keys that can be used for this warning option"
  [option-sym]
  (let [syms (warning-symbols option-sym)]
    nil))

;;---------------------------------------------------------------
;; Pages
;;---------------------------------------------------------------

(defn sym-page
  "Full view of a symbol."
  [sym]
  [:div
    [:h1 {:class (sym-doc-progress-color sym)}
      (docname-display (:full-name sym))]
    (when-let [name (:known-as sym)]
      [:em "known as " name])
    (when-let [full-name (:moved sym)]
      [:em [:strong "MOVED"] ", please see " full-name])
    [:table
      [:tr
        [:td (if (= "option" (:type sym))
               ({"compiler-options" "compiler option"
                 "repl-options" "repl option"} (:ns sym))
               (:type sym))]
        [:td (history-string (:history sym))]
        (when-let [{:keys [full-name url]} (:clj-equiv sym)]
          [:td
            (when (= "clojure" (-> sym :source :repo))
              "imported ")
            [:a {:href url}
              [:img {:src (str *root* "/img/clojure-icon.gif")
                     :height "24px"
                     :valign "middle"}]
              " " full-name]])
        (when-let [{:keys [clj-url edn-url]} (:syntax-equiv sym)]
          (list
            (when clj-url
              [:td
                [:a {:href clj-url}
                  [:img {:src (str *root* "/img/clojure-icon.gif")
                         :height "24px"
                         :valign "middle"}]
                  " in clojure"]])
            (when edn-url
              [:td [:a {:href edn-url} " in edn"]])))]]

    (when-let [usage (seq (:usage sym))]
      (list
        (for [u usage]
          [:div [:code u]])
        [:hr]))
    (when-let [summary (:summary sym)]
      (list
        [:div (markdown-with-doc-biblio summary (:md-biblio sym))]
        [:hr]))
    (when-let [details (:details sym)]
      (list
        [:h3 "Details:"]
        [:div (markdown-with-doc-biblio details (:md-biblio sym))]
        [:hr]))
    (when-let [examples (:examples sym)]
      (list
        [:h3 "Examples:"]
        [:div (markdown-with-doc-biblio examples (:md-biblio sym))]
        [:hr]))
    (when-let [see-also (seq (:see-also sym))]
      (list
        [:h3 "See Also:"]
        [:ul
          (for [docname see-also]
            [:li [:a {:href (str *root* (docname-url docname))}
                   (docname-display docname)]])]
        [:hr]))
    (when-let [docstring (:docstring sym)]
      (list
        "Source docstring:"
        [:pre docstring]))
    (when-let [source (:source sym)]
      (sym-source source))
    (when-let [extra-sources (seq (:extra-sources sym))]
      (for [source extra-sources]
        (sym-source source)))

    [:div
      [:a {:href (:edit-url sym)} "Edit Here!"]]])

(defn sym-fallback-summary
  "If a symbol has no summary, we create one using Usage and Docstring."
  [sym]
  (list
    (when-let [usage (seq (:usage sym))]
      [:div.sep
        (for [u usage]
          [:div [:code u]])])
    (when-let [docstring (:docstring sym)]
      [:div.sep [:pre docstring]])))

(defn sym-preview
  "Preview of a symbol."
  [sym]
  [:div {:style "position: relative;"}
    (let [id (:name-encode sym)
          title (or (:display-as sym) (:name sym))]
      [:div {:id id :class (sym-doc-progress-color sym)}
        [:strong  title]
        (when-let [name (:known-as sym)]
          [:em "- known as " name])
        " - " (:type sym)])
    [:div {:style "position: absolute; right: 0; top: 0;"}
      [:a {:href (str *root* (urls/pretty (urls/api-sym (:ns sym) (:name-encode sym))))} "full details >"]]
    [:div.sep]
    (or
      (when-let [summary (:summary sym)]
        [:div (markdown-with-doc-biblio summary (:md-biblio sym) :preview? true)])
      (sym-fallback-summary sym))])

(defn option-sym-preview
  "Preview of an option symbol."
  [sym]
  [:div {:style "position: relative;"}
    (let [id (:name sym)]
      [:div {:id id :class (sym-doc-progress-color sym)}
        [:strong ":" id]])
    [:div {:style "position: absolute; right: 0; top: 0;"}
      [:a {:href (str *root* (urls/pretty (urls/api-sym (:ns sym) (:name-encode sym))))} "full details >"]]
    [:div.sep]
    (when-let [summary (:summary sym)]
      [:div (markdown-with-doc-biblio summary (:md-biblio sym) :preview? true)])])

(defn ns-page-body
  [api-type ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        title (or (:display-as ns-data) ns-)
        syms (get-ns-symbols api-type ns-)
        main-syms (remove type-or-protocol? syms)
        type-syms (filter type-or-protocol? syms)]
    [:div
      [:h2 title]
      (when-not (get #{"cljs.core"} ns-)
        [:div (history-string (:history ns-data))])
      [:div.sep]
      (when-let [details (:details ns-data)]
        [:div (markdown-with-doc-biblio details (:md-biblio ns-data) :preview? true)])
      [:hr]
      (interpose [:hr]
        (for [sym main-syms]
          (sym-preview sym)))
      (when (seq type-syms)
        (list
          [:div.sep]
          [:h4 "Types and Protocols"]
          [:hr]
          (interpose [:hr]
            (for [sym type-syms]
              (sym-preview sym)))))]))

(defn ns-page
  "Full view of the namespace."
  [api-type ns-]
  (sidebar-layout
    (ns-sidebar api-type ns-)
    (ns-page-body api-type ns-)))

(defn syntax-ns-page-body []
  (let [ns- "syntax"
        ns-data (get-in api [:namespaces ns-])
        title (or (:display-as ns-data) ns-)]
    [:div
      [:h2 title]
      [:div.sep]
      (when-let [details (:details ns-data)]
        [:div (markdown-with-doc-biblio details (:md-biblio ns-data) :preview? true)])
      [:hr]
      (interpose [:hr]
        (for [category (:syntax categories)]
          (list
            [:h4 (:title category)]
            (interpose [:hr]
              (for [full-name (:entries category)]
                (let [sym (get-in api [:symbols full-name])]
                  (sym-preview sym)))))))]))

(defn syntax-ns-page
  "Full view of the syntax namespace."
  []
  (sidebar-layout
    (syntax-ns-sidebar)
    (syntax-ns-page-body)))

(defn options-ns-page-body [ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        title (or (:display-as ns-data) ns-)
        syms (get-ns-symbols :options ns-)]
    [:div
      [:h2 title]
      [:div.sep]
      (when-let [details (:details ns-data)]
        [:div (markdown-with-doc-biblio details (:md-biblio ns-data) :preview? true)])
      [:hr]
      (interpose [:hr]
        (for [sym syms]
          (option-sym-preview sym)))]))

(defn options-ns-page
  "Full view of the options namespace."
  [ns-]
  (sidebar-layout
    (options-ns-sidebar ns-)
    (options-ns-page-body ns-)))

(defn ns-preview
  "Preview of a namespace."
  [api-type ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        ns-url (if (= api-type :compiler) urls/api-compiler-ns urls/api-ns)
        title (or (:display-as ns-data) ns-)
        syms (get-ns-symbols api-type ns-)
        main-syms (remove type-or-protocol? syms)
        type-syms (filter type-or-protocol? syms)]
    (list
      [:h4 [:a {:href (str *root* (urls/pretty (ns-url ns-)))} title]]
      [:p (:summary ns-data)]
      (interpose " | "
        (for [sym-data main-syms]
          (let [name- (or (:display-as sym-data) (:name sym-data))]
            [:span {:class (sym-doc-progress-color sym-data)}
              [:a {:href (str *root* (urls/pretty (urls/api-sym-prev api-type ns- (:name-encode sym-data))))} name-]])))
      (when (seq type-syms)
        (list
          [:div.sep]
          [:span "Types and Protocols: "]
          (interpose " | "
            (for [sym-data type-syms]
              (let [name- (or (:display-as sym-data) (:name sym-data))]
                [:span {:class (sym-doc-progress-color sym-data)}
                  [:a {:href (str *root* (urls/pretty (urls/api-sym-prev api-type ns- (:name-encode sym-data))))} name-]]))))))))

(defn syntax-ns-preview
  "Preview of the syntax namespace."
  []
  (let [ns- "syntax"
        ns-data (get-in api [:namespaces ns-])
        title (or (:display-as ns-data) ns-)]
    (list
      [:h4 [:a {:href (str *root* (urls/pretty (urls/api-ns ns-)))} title]]
      [:table
        (for [category (:syntax categories)]
          [:tr
            [:td (:title category) ": "]
            [:td
              (interpose " | "
                (for [sym (:entries category)]
                  (let [sym-data (get-in api [:symbols sym])
                        name- (or (:display-as sym-data) (:name sym-data))]
                    [:span {:class (sym-doc-progress-color sym-data)}
                      [:a {:href (str *root* (urls/pretty (urls/api-sym-prev :syntax ns- (:name-encode sym-data))))} name-]])))]])])))

(defn options-ns-preview
  "Preview of the options namespace."
  [ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        title (or (:display-as ns-data) ns-)
        syms (get-ns-symbols :options ns-)]
    (list
      [:h4 [:a {:href (str *root* (urls/pretty (urls/api-ns ns-)))} title]]
      (interpose " | "
        (for [sym-data syms]
          (let [name- (:display-as sym-data)]
            [:span {:class (sym-doc-progress-color sym-data)}
                [:a {:href (str *root* (urls/pretty (urls/api-sym-prev :options ns- (:name-encode sym-data))))} name-]]))))))

(defn index-page-body []
  [:div
    [:h2 "ClojureScript API"]
    [:p
      "Welcome! This is a comprehensive reference for ClojureScript's"
      " syntax, standard library, and compiler API. "
      [:strong
       [:a {:href "http://cljs.info/cheatsheet" :target "_blank"}
        "See the Cheatsheet for quick reference"]]]
    [:p
      "Documentation is versioned and supplemented by curated descriptions,"
      "examples, and cross-refs.  Community contributions welcome."]
    [:p [:strong "Current Version:"] " " version
      (when (master-version? version)
        (str " (master)"))
      " | " [:a {:href (str *root* (urls/pretty urls/versions))} "Version Table"]]
    (when-not *docset?*
      [:p.dash-section
        [:img.dash-logo {:src (str *root* "/img/dash.png")}]
        "Get these docs for "
        [:a {:href "https://kapeli.com/dash"} "Dash"]
        " under User Contributed downloads."])
    [:hr]
    (syntax-ns-preview)
    [:hr]
    (interpose [:hr]
      (for [ns- (options-namespaces)]
        (options-ns-preview ns-)))
    [:hr]
    [:h2 "Namespaces"]
    (interpose [:hr]
      (for [ns- (lib-namespaces)]
        (ns-preview :library ns-)))
    [:hr]
    [:h2 "Compiler"]
    (interpose [:hr]
      (for [ns- (compiler-namespaces)]
        (ns-preview :compiler ns-)))])

(defn index-page []
  (sidebar-layout
    (index-sidebar)
    (index-page-body)))

(defn create-sym-page! [{:keys [ns name-encode] :as sym}]
  (->> (sym-page sym)
       (common-layout {:head {:title (str "CLJS - " (docname-display (:full-name sym)))}})
       (hiccup/render)
       (urls/write! (urls/api-sym ns name-encode))))

(defn create-ns-page! [api-type ns-]
  (let [filename (urls/api-ns* api-type ns-)]
    (urls/make-dir! filename)
    (let [page (cond
                 (= ns- "syntax") (syntax-ns-page)
                 (= api-type :options) (options-ns-page ns-)
                 :else (ns-page api-type ns-))
          title (or (get-in api [:namespaces ns- :display-as]) ns-)]
      (->> page
           (common-layout {:head {:title (str "CLJS - " title)}})
           (hiccup/render)
           (urls/write! filename)))))

(defn create-index-page! []
  (->> (index-page)
       (common-layout {:head {:title "CLJS API"}})
       (hiccup/render)
       (urls/write! urls/api-index)))

(defn render! []
  (doseq [ns (keys (:namespaces api))]
    (urls/make-dir! (urls/api-ns ns)))

  (doseq [api-type [:syntax :options :library :compiler]]
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
