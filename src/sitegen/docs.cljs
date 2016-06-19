(ns sitegen.docs
  (:require
    [util.hiccup :as hiccup]
    [sitegen.urls :as urls]
    [sitegen.layout :refer [common-layout]]))

(defn index-page []
  [:div
    [:h2 "Documentation"]
    [:ul
      [:li [:a {:href (urls/pretty urls/api-index)} "Versioned API/Syntax Reference"]]
      [:li [:a {:href "http://cljs.info/cheatsheet/"} "CheatSheet"]]]])

(defn create-index-page! []
  (->> (index-page)
       (common-layout)
       (hiccup/render)
       (urls/write! urls/docs-index)))

(defn render! []
  (create-index-page!))
