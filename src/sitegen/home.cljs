(ns sitegen.home
  (:require
    [util.hiccup :as hiccup]
    [sitegen.layout :refer [common-layout]]
    [sitegen.urls :as urls]))

(defn home []
  [:div
    [:script
      (str "location.href = "
           \"
           (urls/pretty urls/news-index)
           \")]])

(defn _404 []
  [:div {:style "text-align: center"}
    [:h4 "404"]
    [:a {:href "https://twitter.com/fogus/status/553577677022834688"}
      [:img {:src "https://pbs.twimg.com/media/B66z9r7IcAA5m_d.png"}]]])

(defn render-home! []
  (->> (home)
       (common-layout nil)
       (hiccup/render)
       (urls/write! urls/home)))

(defn render-404! []
  (->> (_404)
       (common-layout nil)
       (hiccup/render)
       (urls/write! urls/_404)))

(defn render! []
  (render-home!)
  (render-404!))
