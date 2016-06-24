(ns sitegen.layout)

(declare head)
(declare body-header)
(declare body-footer)

(defn common-layout [content]
  [:html
    (head)
    [:body
      [:div.container
        (body-header)
        content
        (body-footer)]]])

(defn sidebar-layout [& columns]
  (case (count columns)
    1 (first columns)
    2 [:div.container
        [:div.row
          [:div.three.columns {:style "overflow-x: hidden"} (first columns)]
          [:div.nine.columns (second columns)]]]
    3 [:div.container
        [:div.row
          [:div.three.columns (first columns)]
          [:div.three.columns (second columns)]
          [:div.six.columns (nth columns 2)]]]
    nil))

(defn head
  [{:keys [title description]}]
  [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]

    [:title title]
    [:meta {:name "description" :content description}]

    [:link {:rel "stylesheet" :href "http://fonts.googleapis.com/css?family=Raleway:400,300,600,700"}]
    [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css"}]
    [:link {:rel "stylesheet" :href "/css/normalize.css"}]
    [:link {:rel "stylesheet" :href "/css/skeleton.css"}]
    [:link {:rel "stylesheet" :href "/css/custom.css"}]
    [:link {:rel "stylesheet" :href "/css/github-theme.css"}]

    ;[:link {:rel "canonical" :href "{{ page.url | replace:'index.html','' | prepend: site.baseurl | prepend: site.url }}"}]
    ;[:link {:rel "alternate" :type "application/rss+xml" :title "{{ site.title }}" :href "{{ "/feed.xml" | prepend: site.baseurl | prepend: site.url }}" /}]

    [:link {:rel "apple-touch-icon" :sizes "57x57" :href "/img/icons/apple-touch-icon-57x57.png"}]
    [:link {:rel "apple-touch-icon" :sizes "114x114" :href "/img/icons/apple-touch-icon-114x114.png"}]
    [:link {:rel "apple-touch-icon" :sizes "72x72" :href "/img/icons/apple-touch-icon-72x72.png"}]
    [:link {:rel "apple-touch-icon" :sizes "144x144" :href "/img/icons/apple-touch-icon-144x144.png"}]
    [:link {:rel "apple-touch-icon" :sizes "60x60" :href "/img/icons/apple-touch-icon-60x60.png"}]
    [:link {:rel "apple-touch-icon" :sizes "120x120" :href "/img/icons/apple-touch-icon-120x120.png"}]
    [:link {:rel "apple-touch-icon" :sizes "76x76" :href "/img/icons/apple-touch-icon-76x76.png"}]
    [:link {:rel "apple-touch-icon" :sizes "152x152" :href "/img/icons/apple-touch-icon-152x152.png"}]
    [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/img/icons/apple-touch-icon-180x180.png"}]
    [:link {:rel "icon" :type "image/png" :href "/img/icons/favicon-192x192.png" :sizes "192x192"}]
    [:link {:rel "icon" :type "image/png" :href "/img/icons/favicon-160x160.png" :sizes "160x160"}]
    [:link {:rel "icon" :type "image/png" :href "/img/icons/favicon-96x96.png" :sizes "96x96"}]
    [:link {:rel "icon" :type "image/png" :href "/img/icons/favicon-16x16.png" :sizes "16x16"}]
    [:link {:rel "icon" :type "image/png" :href "/img/icons/favicon-32x32.png" :sizes "32x32"}]])

(defn body-header []
  [:nav.navbar
    [:div.container
      [:ul.navbar-list
        [:li.navbar-item [:a.navbar-link-logo [:img.navbar-logo {:src "/img/cljs-white.svg"}]]]
        [:li.navbar-item [:a.navbar-title "ClojureScript"]]
        [:li.navbar-item [:a.navbar-link {:href "/api"} "API"]]
        [:li.navbar-item [:a.navbar-link {:href "/news"} "News"]]
        [:li.navbar-item [:a.navbar-link {:href "https://github.com/clojure/clojurescript" :target "_blank"} "GitHub"]]]]])

(defn body-footer []
  [:footer.site-footer
    [:div.wrapper
      [:hr]
      [:div.cljs-legal
        "ClojureScript is licensed "
        [:a {:href "http://opensource.org/licenses/eclipse-1.0.php"} "EPL 1.0"]
        [:br]
        "Copyright © Rich Hickey"]]])
