(ns util.hiccup
  (:require
    [hiccups.runtime :as hiccups]))

(defn render [x]
  (hiccups/render-html x))
