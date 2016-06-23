(ns sitegen.home
  (:require
    [util.hiccup :as hiccup]
    [sitegen.layout :refer [common-layout]]
    [sitegen.urls :as urls]))

(defn home []
  [:div
    [:script
      (str "location.href = "
           \"
           (urls/pretty urls/news-index)
           \")]])

(defn render! []
  (->> (home)
       (common-layout)
       (hiccup/render)
       (urls/write! urls/home)))
