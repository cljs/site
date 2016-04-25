(ns util.console
  (:require
    [clojure.string :as string]))

(def readline (js/require "readline"))
(def stdout js/process.stdout)

(def dir {:left -1 :right 1 :entire 0})

(defn replace-line [& args]
  (.cursorTo readline stdout 0)
  (.write stdout (string/join " " args))
  (.clearLine readline stdout (dir :right)))
