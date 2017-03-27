(ns client.core
  (:require
    goog.uri.utils))

;; We want ?progress to color-code symbols based on documentation progress.
(when (or (= (aget js/localStorage "progress") "true")
          (goog.uri.utils.hasParam js/location.href "progress"))
  (js/document.body.classList.add "progress"))
