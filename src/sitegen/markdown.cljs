(ns sitegen.markdown)

(def commonmark (js/require "commonmark"))
(def reader (new (.-Parser commonmark)))
(def writer (new (.-HtmlRenderer commonmark)))

(defn md->html [md]
  (let [parsed (.parse reader md)
        result (.render writer parsed)]
    result))
