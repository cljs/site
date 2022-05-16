(ns util.markdown
  (:require
    [clojure.string :as str]
    [hiccup.core :as hiccup]
    [util.highlight :as highlight])
  (:import
    [org.commonmark.node Node FencedCodeBlock]
    [org.commonmark.parser Parser]
    [org.commonmark.renderer NodeRenderer]
    [org.commonmark.renderer.html CoreHtmlNodeRenderer HtmlRenderer HtmlNodeRendererFactory]))

(defmulti render-node (fn [ctx node] (type node)))

(defmethod render-node FencedCodeBlock [ctx node]
  ;; Render a fenced code block using our syntax highlighter.
  (doto (.getWriter ctx)
    (.line)
    (.raw (hiccup/html
            (highlight/syntax-code-block
              (.getLiteral node) ;; code
              (.getInfo node)))) ;; lang
    (.line)))

;; Thanks to Ethan McCue for figuring out how to translate this to Clojure interop:
;; https://github.com/commonmark/commonmark-java#customize-html-rendering
(def html-renderer
  (-> (HtmlRenderer/builder)
      (.nodeRendererFactory
        (reify HtmlNodeRendererFactory
          (create [_ ctx]
            (reify NodeRenderer
              (getNodeTypes [_]      #{FencedCodeBlock})
              (render       [_ node] (render-node ctx node))))))
      (.build)))

(def parser (.build (Parser/builder)))

(defn render [md]
  (->> md
       (.parse parser)
       (.render html-renderer)))

(comment
  (render " This is [foo].

```clj
123
;; => 123
```

[foo]:../../api/syntax/syntax_destructure-vector.html"))
