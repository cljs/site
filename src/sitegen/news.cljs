(ns sitegen.news
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [planck.core :refer [spit slurp]]
    [hiccups.runtime :refer [render-html]]
    [markdown.core :refer [md->html]]
    [sitegen.layout :refer [common-layout]]
    [sitegen.urls :as urls]
    [goog.string])
  (:import
    goog.i18n.DateTimeFormat))

;;---------------------------------------------------------------
;; Post Retrieval
;;---------------------------------------------------------------

(def posts [])
(def index-filename "news/index.edn")
(def md-dir "news/")

(defn transform-jira [md-text]
  (string/replace
    md-text
    #"\bCLJS-\d+\b"
    #(str "[" % "](http://dev.clojure.org/jira/browse/" % ")")))

(defn add-body
  [{:keys [filename] :as post}]
  (let [md-body (->> (str md-dir filename)
                     (slurp)
                     (transform-jira))
        html-body (md->html md-body)]
    (assoc post
      :md-body md-body
      :html-body html-body)))

(defn add-date
  [{:keys [filename] :as post}]
  (let [[_ y m d] (re-find #"^(\d\d\d\d)-(\d\d)-(\d\d)" filename)
        y (js/parseInt y)
        m (js/parseInt m)
        d (js/parseInt d)
        date (js/Date. y (dec m) d)]
    (assoc post :date date)))

(defn add-url
  [{:keys [filename] :as post}]
  (let [[_ title] (re-find #"^(.*)\.md$" filename)
        url (urls/news-post title)]
    (assoc post :url url)))

(def transform-post
  (comp add-body
        add-date
        add-url))

(defn update! []
  (->> (slurp index-filename)
       (read-string)
       (map transform-post)
       (set! posts)))

;;---------------------------------------------------------------
;; Page Rendering
;;---------------------------------------------------------------

;; Internationalized Dates
;; http://www.closurecheatsheet.com/i18n#goog-i18n-datetimeformat
;; http://google.github.io/closure-library/api/enum_goog_i18n_DateTimeFormat_Format.html
(def date-format (DateTimeFormat. DateTimeFormat.Format.MEDIUM_DATE))
(defn date-str [date]
  (.format date-format date))

(defn index-page []
  [:table
    (for [post (reverse posts)]
      [:tr
        [:td (date-str (:date post))]
        [:td [:a {:href (urls/pretty (:url post))} (:title post)]]])])

(defn post-meta
  [{:keys [title date author google_group_msg
           external_source external_name github_pre_release]}]
  (let [elements
        [(date-str date)
         (when author
           (str "by " author))
         (when google_group_msg
           [:a {:href (str "https://groups.google.com/d/msg/" google_group_msg)}
            "on Google Groups"])
         (when external_source
           [:a {:href external_source}
            external_name])
         (when github_pre_release
           [:a {:href (str "https://github.com/clojure/clojurescript/releases/tag/" github_pre_release)}
            "on GitHub"])]]
    (->> elements
         (filter identity)
         (interpose " "))))

(defn post-page
  [{:keys [title release_version html-body] :as post}]
  [:div
    [:h1 title]
    [:p (post-meta post)]
    [:article
      (when release_version
        [:p "Leiningen dependency information:"
          [:pre [:code (str "[org.clojure/clojurescript " release_version)]]])
      html-body]])

(defn rss-date [date]
  (.toUTCString date))

(defn rss-feed-xml []
  (let [now (rss-date (js/Date.))]
    [:rss {:xmlns:atom "http://www.w3.org/2005/Atom" :version "2.0"}
     [:channel
      [:title "ClojureScript"]
      [:description "ClojureScript News and Releases"]
      [:link "http://cljsinfo.github.io/news"]
      [:atom:link {:href (str urls/root urls/news-feed)
                   :rel "self"
                   :type "application/rss+xml"}]
      [:pubDate now]
      [:lastBuildDate now]
      [:generator "(sitegen.news/rss-feed-xml)"]
      (for [{:keys [title html-body date url]}
            (take 10 (reverse posts))]
        [:item
          [:title title]
          [:description (goog.string.htmlEscape html-body true)]
          [:pubDate (rss-date date)]
          [:link (urls/pretty (str urls/root url))]])]]))

(defn create-index-page! []
  (->> (index-page)
       (common-layout)
       (render-html)
       (urls/write! urls/news-index)))

(defn create-post-pages! []
  (doseq [post posts]
    (->> (post-page post)
         (common-layout)
         (render-html)
         (urls/write! (:url post)))))

(defn create-rss-feed! []
  (->> (rss-feed-xml)
       (render-html)
       (urls/write! urls/news-feed)))

(defn render! []
  (urls/make-dir! urls/news-dir)
  (create-index-page!)
  (create-post-pages!)
  (create-rss-feed!))
