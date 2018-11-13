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
                         get-item
                         master-version?
                         version
                         version-has-news-post?
                         hide-lib-ns?
                         lib-namespaces
                         compiler-namespaces
                         options-namespaces
                         get-ns-symbols
                         type-or-protocol?
                         docname-url
                         docname-display
                         categorize-syms]]))

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
        cats (categorize-syms ns- syms)]
    [:div
      [:a {:href (str *root* (urls/pretty urls/api-index))} "< Back to Overview"]
      [:div.sep]
      (for [cat cats]
        (list
          [:div.sep (:title cat)]
          (for [full-name (:entries cat)]
            (let [sym (get-in api [:symbols full-name])
                  parent-type (:parent-type sym)
                  name- (if parent-type
                          (subs (:name sym) (inc (count parent-type)))
                          (or (:display-as sym) (:name sym)))]
              [:div {:class (sym-doc-progress-color sym)}
                [:span {:style "opacity: 0.3"} (when parent-type "└")]
                [:a {:href (str "#" (:name-encode sym))} name-]]))))]))

;;---------------------------------------------------------------
;; Page Utils
;;---------------------------------------------------------------

(defn history-block
  [{:keys [history moved-from] :as item}]
  (for [[change version] (reverse history)]
    (case change
      "+" (if moved-from
            (list
              "previously "
              [:a {:href (urls/pretty (urls/api-item (get-item moved-from)))} moved-from])
            (if (= 1 (count history))
              (str "since v" version)
              (str "added v" version)))
      "-" (str "removed v" version))))

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
    (str body "\n\n"
      (string/join "\n"
        (for [docname biblio]
          (str "[doc:" docname "]:" (str *root* (docname-url docname :preview? preview?))))))))

;;---------------------------------------------------------------
;; Pages
;;---------------------------------------------------------------

(defn sub-option-preview
  "The only view for a sub-option symbol is a preview."
  [sym]
  [:div {:style "position: relative;"}
    (let [id (:name sym)]
      [:div {:id id :class (sym-doc-progress-color sym)}
        [:strong (str ":" id)]])
    [:div {:style "position: absolute; right: 0; top: 0;"}
      [:span {:style "opacity:0.3"} (interpose ", " (history-block sym))]
      " | "
      [:a {:href (:edit-url sym)} "Edit"]]
    [:div.sep]
    (when-let [summary (:summary sym)]
      [:div (markdown-with-doc-biblio summary (:md-biblio sym))])])

(defn sym-page
  "Full view of a symbol."
  [sym]
  [:div
    [:h1 {:class (sym-doc-progress-color sym)}
      (cond->> (docname-display (:full-name sym))
        (:removed sym) (vector :s))]
    (when-let [name (:known-as sym)]
      [:em "known as \"" name "\""])
    (when-let [moved-to (:moved-to sym)]
      [:div
        [:em [:strong "MOVED"] ", please see "
          [:a {:href (urls/pretty (urls/api-item (get-item moved-to)))} moved-to]]])
    [:table
      [:tr
        [:td (if (= "option" (:type sym))
               ({"compiler-options" "compiler option"
                 "repl-options" "repl option"} (:ns sym))
               (:type sym))]
        (for [h (history-block sym)]
          [:td h])
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
              [:td [:a {:href edn-url} " in edn"]])))
        [:td [:a {:href (:edit-url sym)} "Edit"]]]]

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
    (when-let [sub-options-ns (:sub-options-ns sym)]
      (list
        [:h3 "Options:"]
        (interpose [:hr]
          (for [opt (get-ns-symbols :options sub-options-ns)]
            (sub-option-preview opt)))
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
        (sym-source source)))])

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
        [:strong title]
        (when-let [name (:known-as sym)]
          [:em "- \"" name "\""])
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
        cats (categorize-syms ns- syms)]
    [:div
      [:h2
        (cond->> title
          (:removed ns-data) (vector :s))]
      (when-let [moved-to (:moved-to ns-data)]
        [:div
          [:em [:strong "MOVED"] ", please see "
            [:a {:href (urls/pretty (urls/api-item (get-item moved-to)))} moved-to]]])
      (when-not (get #{"cljs.core" "syntax" "compiler-options" "repl-options"} ns-)
        [:div (interpose [:br] (history-block ns-data))])
      [:div.sep]
      (when-let [details (:details ns-data)]
        [:div (markdown-with-doc-biblio details (:md-biblio ns-data) :preview? true)])
      [:hr]
      (interpose [:hr]
        (for [cat cats]
          (list
            [:h4 (:title cat)]
            (interpose [:hr]
              (for [full-name (:entries cat)]
                (let [sym (get-in api [:symbols full-name])]
                  (sym-preview sym)))))))]))

(defn ns-page
  "Full view of the namespace."
  [api-type ns-]
  (sidebar-layout
    (ns-sidebar api-type ns-)
    (ns-page-body api-type ns-)))

(defn ns-preview
  "Preview of a namespace."
  [api-type ns-]
  (let [ns-data (get-in api [:namespaces ns-])
        ns-url (if (= api-type :compiler) urls/api-compiler-ns urls/api-ns)
        title (or (:display-as ns-data) ns-)
        syms (get-ns-symbols api-type ns-)
        cats (categorize-syms ns- syms)]
    (list
      [:h4 [:a {:href (str *root* (urls/pretty (ns-url ns-)))} title]]
      [:p (:summary ns-data)]
      [:table
        (for [cat cats]
          (let [empty-title? (= (:title cat) "")]
            [:tr
              (when-not empty-title?
                [:td {:style "vertical-align:top; width:128px"}
                  (:title cat) ": "])
              [:td {:colspan (if empty-title? 2 1)}
                (interpose " | "
                  (for [sym (:entries cat)]
                    (let [sym-data (get-in api [:symbols sym])
                          name- (or (:display-as sym-data) (:name sym-data))]
                      [:span {:class (sym-doc-progress-color sym-data)}
                        [:a {:href (str *root* (urls/pretty (urls/api-sym-prev api-type ns- (:name-encode sym-data))))} name-]])))]]))])))

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
    [:style "tr:last-child td {border-bottom:0}"]
    [:hr]
    (ns-preview :syntax "syntax")
    [:hr]
    (interpose [:hr]
      (for [ns- (options-namespaces)]
        (ns-preview :options ns-)))
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
    (let [page (ns-page api-type ns-)
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

(defn ns-has-page? [ns-]
  (not (get-in api [:namespaces ns- :sub-options-sym])))

(defn sym-has-page? [sym]
  (ns-has-page? (:ns sym)))

(defn render! []
  (doseq [ns- (keys (:namespaces api))
          :when (ns-has-page? ns-)]
      (urls/make-dir! (urls/api-ns ns-)))

  (doseq [api-type [:syntax :options :library :compiler]]
    (doseq [ns- (get-in api [:api api-type :namespace-names])
            :when (ns-has-page? ns-)]
      (create-ns-page! api-type ns-)))

  (let [syms (->> (vals (:symbols api))
                  (sort-by :full-name))]
    (create-index-page!)
    (doseq [sym syms :when (sym-has-page? sym)]
      (console/replace-line "Creating page for" (:full-name sym))
      (create-sym-page! sym))
    (console/replace-line "Done creating API pages."))
  (println))
