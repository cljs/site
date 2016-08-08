(ns util.markdown
  (:require
    [util.highlight :refer [highlight-code]]
    [util.hiccup :as hiccup]
    [clojure.string :as string]))

(def marked (js/require "marked"))

(def renderer (marked.Renderer.))
(set! renderer.link
  (fn [href title text]
    (let [className (when (string/starts-with? text "<code") "code-link")]
      (hiccup/render
        [:a {:href href :title title :class className} text]))))

(.setOptions marked
  #js {:highlight highlight-code
       :renderer renderer})

(defn render [md]
  (marked md))
