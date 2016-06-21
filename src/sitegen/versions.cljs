(ns sitegen.versions
  (:require
    [util.hiccup :as hiccup]
    [sitegen.urls :as urls]
    [sitegen.api :refer [api version-has-news-post?]]
    [sitegen.layout :refer [common-layout]]))

(defn abbrev-gclosure-lib
  [version]
  (if-let [[_ prefix] (re-find #"(0\.0-\d+)-.+" version)]
    prefix
    version))

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
      (for [version (get-in api [:history :versions])
            :let [details (get-in api [:history :details version])]]
        [:tr
          [:td (if (version-has-news-post? version)
                  [:a {:href (urls/pretty (urls/news-post version))} version]
                  version)]
          [:td (:date details)]
          [:td (:clj-version details)]
          [:td (:treader-version details)]
          [:td (:gclosure-com details)]
          [:td (abbrev-gclosure-lib (:gclosure-lib details))]])]])

(defn create-versions-page! []
  (->> (versions-page)
       (common-layout)
       (hiccup/render)
       (urls/write! urls/versions)))

(defn render! []
  (create-versions-page!))
