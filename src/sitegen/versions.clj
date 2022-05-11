(ns sitegen.versions
  (:require
    [hiccup.core :refer [html]]
    [sitegen.urls :as urls :refer [*root*]]
    [sitegen.api :refer [api version master-version?]]
    [sitegen.layout :refer [common-layout]]))

(defn abbrev-gclosure-lib
  [v]
  (if-let [[_ prefix] (re-find #"(0\.0-\d+)-.+" v)]
    prefix
    v))

(defn version-display [v]
  (cond
    (master-version? v)
    [:span {:style "opacity: 0.5"} v]

    :else v))

(defn master-comment []
  (when (master-version? version)
    (let [tag (get-in api [:history :details version :tag])
          prev-version (second (re-find #"(.*)\+$" version))
          commits-ahead (second (re-find #"-(\d+)-g" tag))]
     [:div {:style "text-align: center"}
       " ^ "
       [:a {:href (str "https://github.com/clojure/clojurescript/tree/" tag)}
        commits-ahead " unpublished commits"]
       " on master."])))

(defn versions-page []
  [:div
    [:h2 "Version Table"]
    [:p
      "This is a detailed table of ClojureScript versions and dependencies. "]
    [:p
      "Clojure is the language that the ClojureScript compiler is written in. "
      "tools.reader is used by ClojureScript for reading syntax. "
      "Google Closure Compiler is used for build packaging and optimization, "
      "and Google Closure Library is an extensive standard library."]
    [:table
      [:tr
        [:th "Version"]
        [:th "Date"]
        [:th "Clojure"]
        [:th "Reader"]
        [:th "Closure Compiler"]
        [:th "Closure Library"]]
      (for [v (get-in api [:history :versions])]
        (let [details (get-in api [:history :details v])
              row [:tr
                    [:td (version-display v)]
                    [:td (:date details)]
                    [:td (:clj-version details)]
                    [:td (:treader-version details)]
                    [:td (:gclosure-com details)]
                    [:td (abbrev-gclosure-lib (:gclosure-lib details))]]]
          (if (master-version? v)
            (list row [:tr [:td {:colspan 6} (master-comment)]])
            row)))]])

(defn create-versions-page! []
  (->> (versions-page)
       (common-layout {:head {:title "CLJS Versions"}})
       (html)
       (urls/write! urls/versions)))

(defn render! []
  (create-versions-page!))
