(ns util.markdown
  (:require
    [clojure.string :as str])
  (:import
    [org.commonmark.node Node]
    [org.commonmark.parser Parser]
    [org.commonmark.renderer.html HtmlRenderer]))

(def ^Parser parser         (.build (Parser/builder)))
(def ^HtmlRenderer renderer (.build (HtmlRenderer/builder)))

(defn render [md]
  (let [^Node document (.parse parser md)]
    (.render renderer document)))

(comment
  (def s " This is [foo].

[foo]:../../api/syntax/syntax_destructure-vector.html")

  (render s))
