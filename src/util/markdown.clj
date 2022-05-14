(ns util.markdown
  (:require
    [clojure.string :as str]
    [markdown.core :refer [md-to-html-string]]))

(defn render [md]
  (-> md
      (str/replace #"(?m)^\[[^\]]+\]:" #(str %1 " ")) ;; need to add a space in front of reference links "[foo]:bar" -> "[foo]: bar"
      (md-to-html-string :reference-links? true)))

(comment
  (def s " This is [foo][foo].
[foo]:../../api/syntax/syntax destructure-vector.html")

  (render s))
