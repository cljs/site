(ns sitegen.home
  (:require
    [sitegen.layout :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]
    [planck.core :refer [spit]]
    [sitegen.urls :as urls]))

(defn render! []
  (->> (common-layout "")
       (render-html)
       (spit urls/home)))
