(ns sitegen.url)

;; We want the file `site/foo.html` to have a URL `site/foo`.
;; GitHub Pages handles this routing automatically, and so
;; does our node devserver.

;; To make it easier to turn extensions on and off,
;; all links should pass through `url` function.

(def show-ext? false)

(defn url [path]
  (cond-> path
    show-ext? (str ".html")))
