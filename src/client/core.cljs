(ns client.core)

;; On the API index page, we want #progress to color-code symbols based on
;; documentation progress.
(defn on-hash-change []
  (if (= js/location.hash "#progress")
    (js/document.body.classList.add "progress")
    (js/document.body.classList.remove "progress")))

(js/window.addEventListener "hashchange" on-hash-change)
(js/window.addEventListener "load" on-hash-change)
