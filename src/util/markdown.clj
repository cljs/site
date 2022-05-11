(ns util.markdown
  (:require
    [markdown.core :refer [md-to-html-string]]))

(defn render [md]
  (md-to-html-string md))
