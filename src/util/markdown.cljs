(ns util.markdown
  (:require
    [util.highlight :refer [highlight-code]]))

(def marked (js/require "marked"))

(.setOptions marked
  #js {:highlight highlight-code})

(defn render [md]
  (marked md))
