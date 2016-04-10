(ns sitegen.home
  (:require
    [sitegen.html.common :refer [common-layout]]
    [hiccups.runtime :refer [render-html]]
    [planck.core :refer [spit]]))

(defn create-page!
  [out-filename]
  (->> (common-layout "")
       (render-html)
       (spit out-filename)))
