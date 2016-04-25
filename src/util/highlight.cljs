(ns util.highlight)

(def highlightjs (js/require "highlight.js"))

(defn highlight-code [code lang]
  (if lang
    (.-value (.highlight highlightjs lang code))
    code))
