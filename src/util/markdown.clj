(ns util.markdown
  (:require
    [clojure.string :as str]
    [hiccup.core :as hiccup]
    [util.highlight :refer [highlight-code]])
  (:import
    [org.commonmark.node Node FencedCodeBlock Code]
    [org.commonmark.parser Parser]
    [org.commonmark.renderer NodeRenderer]
    [org.commonmark.renderer.html HtmlRenderer HtmlNodeRendererFactory]
    [org.commonmark.ext.gfm.tables TablesExtension]))

;;------------------------------------------------------------------------------
;; Render code blocks and inline code with our highlighter.
;;------------------------------------------------------------------------------

(defmulti render-node (fn [ctx node] (type node)))

(defmethod render-node FencedCodeBlock [ctx node]
  (doto (.getWriter ctx)
    (.line)
    (.raw (hiccup/html [:pre [:code.syntax (highlight-code
                                             (.getLiteral node) ;; code
                                             (.getInfo node))]])) ;; lang
    (.line)))

(defmethod render-node Code [ctx node]
  (doto (.getWriter ctx)
    (.raw (hiccup/html [:code.syntax (highlight-code (.getLiteral node) "clj")]))))

;;------------------------------------------------------------------------------
;; Setup Renderer
;;------------------------------------------------------------------------------

(def extensions
  [(TablesExtension/create)])

;; Thanks to Ethan McCue for figuring out how to translate this to Clojure interop:
;; https://github.com/commonmark/commonmark-java#customize-html-rendering
(def html-renderer
  (-> (HtmlRenderer/builder)
      (.nodeRendererFactory
        (reify HtmlNodeRendererFactory
          (create [_ ctx]
            (reify NodeRenderer
              (getNodeTypes [_]      #{FencedCodeBlock Code})
              (render       [_ node] (render-node ctx node))))))
      (.extensions extensions)
      (.build)))

(def parser
  (-> (Parser/builder)
      (.extensions extensions)
      (.build)))

(defn render [md]
  (->> md
       (.parse parser)
       (.render html-renderer)))


(comment

  (render "
Here is a link: <http://example.com>

Table:

| foo | bar |
|----:|:---:|
| 1   |  2  |

We want to highlight `defn`.

This is a link [foo].

```clj
123
;; => 123
```

[foo]:../../api/syntax/syntax_destructure-vector.html"))
