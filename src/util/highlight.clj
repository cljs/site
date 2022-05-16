(ns util.highlight
  (:require
    [glow.html :as ghtml]
    [glow.parse :as gparse]))

;; See output/css/github-theme.css for styling colors for each token type.

;; maybe update clojure antlr file:
;; https://github.com/antlr/grammars-v4/blob/master/clojure/Clojure.g4)

(defn highlight-code [code lang]
  (if (= "clj" lang)
    (try
      ;; return just the highlighted tokens we can wrap it in an arbitrary element
      (ghtml/hiccup-transform
        (gparse/parse code))
      (catch Exception e
        (println "FAILED TO HIGHLIGHT")
        code))
    code))

(comment
  (highlight-code (slurp "src/util/highlight.clj") "clj"))
