(ns sitegen.markdown)

(def commonmark (js/require "commonmark"))
(def Parser (.-Parser commonmark))
(def HtmlRenderer (.-HtmlRenderer commonmark))

(def reader (Parser.))
(def writer (HtmlRenderer.))

(defn md->html [md]
  (let [parsed (.parse reader md)
        result (.render writer parsed)]
    result))
