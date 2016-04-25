(ns util.markdown)

(def marked (js/require "marked"))
(def highlight (js/require "highlight.js"))

(defn highlight-code [code lang]
  (if lang
    (.-value (.highlight highlight lang code))
    code))

(.setOptions marked
  #js {:highlight highlight-code})

(defn md->html [md]
  (marked md))
