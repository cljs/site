(ns sitegen.doclink
  (:require
    [sitegen.api :refer [api]]))

;;; ================ MARKDOWN SYNTAX ==================
;;;
;;; We use the doclink namenclature as a markdown biblio alias with the `doc:` prefix:
;;;
;;; 1. unnamed:              [doc:cljs.core/foo]      --> name inserted and resolved to biblio link
;;; 2. named:     [some name][doc:cljs.core/foo]      --> resolved to biblio link
;;; 3. ignored:              [doc:cljs.core/foo](...)
;;; 4. ignored:              [doc:cljs.core/foo][...]
;;;
;;; To give these 'doc' forms meaning, we do the following:
;;;
;;;  A: Insert display names for unnamed links:
;;;
;;;     [doc:cljs.core/foo] --> [<short display name>][doc:cljs.core/foo]
;;;
;;;  B: Append biblio links to the end of the body for all detected 'doc' forms:
;;;
;;;     [doc:cljs.core/foo]:<full path to actual page for 'cljs.core/foo'>
;;;

(def doclink-pattern
  "Pattern for potential doclinks in markdown."
  #"\[doc:([^\]]+)\]")

(def unnamed-doclink-pattern
  "Pattern for unnamed potential doclinks in markdown (i.e. not followed by () or [].)
  Example:
  true:
    - [doc:cljs.core/foo]
  false:
    - [doc:cljs.core/foo](example.com)
    - [doc:cljs.core/foo][link-alias]
    - [name][doc:cljs.core/foo]
  "
  ;;    do not allow a preceding "]" char (see #2 above)
  ;;    |
  ;;    |   same as doclink-pattern
  ;;    |   |
  ;;    |   |                do not allow a trailing "[" or "(" chars (see #3 and #4 above)
  ;;    |   |                |
  ;;    |   |                |
  #"(?<!])\[doc:([^\]]+)\](?![\(\[])")

(defn parse-docname
  "foo/bar      <-- normal symbol
   foo          <-- namespace `foo`
   compiler/foo <-- compiler namespace `foo`"
  [docname]
  (let [[a b] ((juxt namespace name) (symbol docname))]
    (cond
      (= a "compiler") {:compiler? true, :ns a}
      (nil? a)         {:ns b}
      :else            {:ns a, :name b})))

(defn docname?
  [docname]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (if name
      (get-in api [:symbols docname])
      (get-in api [:namespaces ns]))))

(defn docname->url
  [docname]
  (let [{:keys [ns name compiler?]} (parse-docname docname)]
    (urls/pretty
      (if name
        (urls/api-symbol ns (get-in api [:symbols docname :name-encode]))
        (if compiler?
          (urls/api-compiler-ns ns)
          (urls/api-ns ns))))))

(defn get-short-display-name
  [docname]
  (let [{:keys [ns name compiler?]} (parse-docname docname)
        display (if name
                  (get-in api [:symbols docname :display])
                  (get-in api [:namespaces ns :display]))]
    (or display name ns)))

(defn insert-doclink-name
  [[whole-match docname]]
  (if (docname? docname)
    (let [name- (get-short-display-name docname)]
      (str "[`" name- "`]" whole-match))
    whole-match))

(defn valid-doclinks
  [md-body]
  (->> md-body
       (re-seq doclink-pattern)
       (map second)
       (filter docname?)))

(defn doclinks-md-biblio
  [md-body]
  (join "\n"
    (for [docname (valid-doclinks md-body)]
      (str "[doc:" docname "]:" (docname->url docname)))))

(defn process-doclinks
  "Process doclinks in given markdown body."
  [md-body]
  (when md-body
    (-> md-body
        (replace unnamed-doclink-pattern insert-doclink-name)
        (str "\n\n" (doclinks-md-biblio md-body)))))

(defn process-all-doclinks
  [item]
  (-> item
      (update-in [:description] process-doclinks)
      (update-in [:examples] (fn [examples]
                               (doall (map #(update-in % [:content] process-doclinks) examples))))))
