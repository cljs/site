(ns sitegen.home
  (:require
    [sitegen.layout :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]
    [sitegen.urls :as urls]))

(defn render! []
  (->> (common-layout "")
       (render-html)
       (urls/write! urls/home)))
