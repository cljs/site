(ns sitegen.home
  (:require
    [util.hiccup :as hiccup]
    [sitegen.layout :refer [common-layout]]
    [sitegen.urls :as urls]))

(defn render! []
  (->> (common-layout "")
       (hiccup/render)
       (urls/write! urls/home)))
