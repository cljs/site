(ns sitegen.doclink
  (:require
    [sitegen.api :refer [api]]))

;; TODO: replace references:
;;   fullname->ns-name
;;   *result*
;;   *doclink-ext*
;;   *doclink-prefix*
;;   encode-fullname => (get-in api [:symbols full-name :name-encode])
;;   get-short-display-name


;;; ================ NAMING CONVENTION ==================
;;;
;;; Whenever we want to reference another doc page in markdown, we use the
;;; following nomenclature:
;;;
;;;   cljs.core/foo         <--- var
;;;
;;; There are compiler namespaces that are not part of the library, so we
;;; label them appropriately:
;;;
;;;   cljs.core             <--- ns in the library API
;;;   compiler/cljs.repl    <--- ns in the compiler API
;;;
;;; The syntax page is its own thing:
;;;
;;;   syntax                <--- syntax forms
;;;
;;; The special forms are also in their own thing:
;;;
;;;   special               <--- special forms ns
;;;

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

(defn valid-doclink?
  [result full-name]
  (let [[a b] (fullname->ns-name full-name)]
    (if (nil? b)
      (= "syntax" a)
      (if (#{"library" "compiler"} a)
        (get-in result [:api (keyword a) :namespace-names b])
        (get-in result [:symbols full-name])))))

(defn doclink-path
  [full-name]
  (let [[a b] (fullname->ns-name full-name)
        ns-link? (or (and (= "syntax" a) (nil? b))
                     (#{"library" "compiler"} a))
        encoded (if ns-link?
                  full-name
                  (encode/encode-fullname full-name))]
    (str *doclink-prefix* encoded *doclink-ext*)))

(defn insert-doclink-name
  [[whole-match full-name]]
  (if (valid-doclink? *result* full-name)
    (let [name- (get-short-display-name full-name)]
      (str "[`" name- "`]" whole-match))
    whole-match))

(defn valid-doclinks
  [md-body]
  (->> md-body
       (re-seq doclink-pattern)
       (map second)
       (filter #(valid-doclink? *result* %))))

(defn doclinks-md-biblio
  [md-body]
  (join "\n"
    (for [full-name (valid-doclinks md-body)]
      (str "[doc:" full-name "]:" (doclink-path full-name)))))

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
