---
release_version: "0.0-3196"
title:  "r0.0-3196 - Conditional Reading, REPLs, and Code Motion"
author: "David Nolen"
google_group_msg: "clojurescript/pdZVL6gAPio/Jtv7WmuEK9QJ"
---

This release is intended to coincide with Clojure 1.7.0-beta1. Besides
conditional reading support, it includes a number of improvements and
bug fixes around REPLs. Piggieback (and thus nREPL) integration has
been greatly enhanced. Also notable is significantly improved cross
module code motion under :modules.

As always feedback welcome!

## 0.0-3196

### Enhancements
* Conditional reading
* map clojure.core/in-ns to REPL in-ns special for existing tools
* CLJS-1171: map clojure.repl/doc, clojure.repl/source, clojure.repl/dir
* add macroexpand and macroexpand-1 macros
* CLJS-1019: REPL source map caching support
* CLJS-1154: Unmunged function names for stacktrace

### Changes
* Clojure 1.7.0-beta1 dependency
* tools.reader 0.9.1 dependency
* CLJS-1188: multi-arity fns hinder cross-module code motion
* cljs.test needs to default to sync
* CLJS-1184: log module building activity under verbose
* CLJS-1175: CLJS defmulti doesn't exhibit same defonce behavior as Clojure's defmulti, suggesting an even better reloading behavior
* CLJS-1176: redirect node REPL output through *out* and *err*, not System/out, System/err
* CLJS-1144 - expose defaul-dispatch-val and dispatch-fn multifn accessors
* CLJ-1172: supply main entry points for all standard REPLs
* less noisy REPL prompt
* add docstrings & validation to macroexpand & macroexpand-1

### Fixes
* CLJS-1192: eliminate JDK8 API dependency in cljs.repl.node
* CLJS-1158: Regression: compiler fails to see symbols defined in another namespace
* CLJS-1189: array-map will return PersistentHashMap if applied to more than (.-HASHMAP-THRESHOLD PersistentArrayMap) pairs
* CLJS-1183: load-file doesn't copy source to output directory
* CLJS-1187: var ast contains internal nodes with bad analysis :context
* CLJS-1182: semantics of load-file should be require + implicit :reload
* CLJS-1179: strange load-file behavior
* CLJS-808: Warning from `find-classpath-lib` mistakenly included in generated source
* CLJS-1169: cannot use REPL load-file on files that declare single segment namespaces
* don't use print unless printing the result of eval
* CLJS-1162: Failure to printStackTrace when REPL initialized
* CLJS-1161: actually print error stack traces to *err*, allow higher-level rebindings of *cljs-ns*
* CLJS-841: cljs.closure/build file locks
* CLJS-1156: load-file fails with :make-reader issue
* CLJS-1152: (require 'some.ns :reload) causes printing to stop working in browser REPL
* CLJS-1157: Stacktrace unmunging blindly use locals
* CLJS-1155: REPL :watch support does not play nicely with :cljs/quit
* CLJS-1137: :cljs/quit fails to actually quit in browser REPL
* CLJS-1148: ClojureScript REPL must maintain eval/print pairing
* make quit-prompt configurable
* CLJS-1149: cljs.repl/repl needs to support :compiler-env option
* CLJS-1140: typo in cljs.repl/repl, `:need-prompt prompt` instead of `:need-prompt need-prompt`
