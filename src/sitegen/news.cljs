(ns sitegen.news
  (:require
    [cljs.reader :refer [read-string]]
    [clojure.string :as string]
    [planck.core :refer [spit slurp]]
    [hiccups.runtime :refer [render-html]]
    [markdown.core :refer [md->html]]
    [sitegen.layout :refer [common-layout]]
    [sitegen.urls :as urls]))

;;---------------------------------------------------------------
;; Post Retrieval
;;---------------------------------------------------------------

(def posts [])
(def index-filename "news/index.edn")
(def md-dir "news/")

(defn add-md-body
  [{:keys [filename] :as post}]
  (let [md-body (slurp (str md-dir filename))]
    (assoc post :md-body md-body)))

(defn add-date
  [{:keys [filename] :as post}]
  (let [[_ y m d] (re-find #"^(\d\d\d\d)-(\d\d)-(\d\d)" filename)
        date {:year (js/parseInt y)
              :month (js/parseInt m)
              :day (js/parseInt d)}]
    (assoc post :date date)))

(defn add-url
  [{:keys [filename] :as post}]
  (let [[_ title] (re-find #"^(.*)\.md$" filename)
        url (urls/news-post title)]
    (assoc post :url url)))

(def transform-post
  (comp add-md-body
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

(def month-str
  {1  "Jan"
   2  "Feb"
   3  "Mar"
   4  "Apr"
   5  "May"
   6  "Jun"
   7  "Jul"
   8  "Aug"
   9  "Sep"
   10 "Oct"
   11 "Nov"
   12 "Dec"})

(defn date-str [{:keys [year month day]}]
  (str (month-str month) " " day ", " year))

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

(defn transform-jira [md-text]
  (string/replace
    md-text
    #"\bCLJS-\d\d\d\d\b"
    #(str "[" % "](http://dev.clojure.org/jira/browse/" % ")")))

(defn post-page
  [{:keys [title release_version md-body] :as post}]
  [:div
    [:h1 title]
    [:p (post-meta post)]
    [:article
      (when release_version
        [:p "Leiningen dependency information:"
          [:pre [:code (str "[org.clojure/clojurescript " release_version)]]])
      (->> md-body
           transform-jira
           md->html)]])

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

(defn render! []
  (create-index-page!)
  (create-post-pages!))
