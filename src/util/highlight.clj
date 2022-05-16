(ns util.highlight
  (:require
    [glow.html :as ghtml]
    [glow.parse :as gparse]))

(def github-colors
  {:background    ""

   ;; parens
   :s-exp         ""

   ;; primitives
   :comment       "#999988"
   :number        "#008080"
   :symbol        ""
   :boolean       "#008080"
   :keyword       "#990073"
   :nil           "#008080"

   ;; strings
   :string        "#dd1144"
   :character     "#dd1144"
   :regex         "#dd1144"

   ;; types of symbols
   ;; see: https://github.com/venantius/glow/tree/master/resources/keywords
   :conditional   "#0086b3"
   :definition    "#0086b3"
   :exception     "#0086b3"
   :core-fn       "#0086b3"
   :macro         "#0086b3"
   :repeat        "#0086b3"
   :special-form  "#0086b3"
   :variable      "#0086b3"

   :reader-char   ""})

(defn highlight-code [code lang]
  (if (= "clj" lang)
    (try
      (ghtml/hiccup-transform
        (gparse/parse code))
      (catch Exception e
        (println "FAILED TO HIGHLIGHT")
        code))
    code))

(defn syntax-code-block [code lang]
  [:div.syntax [:pre [:code (highlight-code code lang)]]])

(defn get-css []
  (ghtml/generate-css github-colors))

(comment
  (highlight-code (slurp "src/util/highlight.clj") "clj")
  (spit "output/css/github-theme.css" (get-css)))
