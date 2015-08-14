---
layout: post
title:  "r0.0-3308 - Fixes and Enhancements"
---

([original Mailing List post](https://groups.google.com/d/msg/clojurescript/kNIKpsFgvyk/y1u_AjChLfYJ))

Leiningen dependency information:

    [org.clojure/clojurescript "0.0-3308"]

This release bumps the Clojure dependecy to 1.7.0-RC1 and includes fixes and minor
enhancements.

As always feedback welcome!

## 0.0-3308

## Changes
* Clojure 1.7.0-RC1 dependency
* CLJS-1292: Add IPrintWithWriter implementation for TaggedLiteral
* add cljs.core/random-uuid
* flush immediately when forwarding Node process out & err
* CLJS-1256 cache UUID hash value
* CLJS-1226: Added the :end-run-test event to cljs.test and a dummy event handler for it

## Fixes
* CLJS-1200: compare behaves differently from Clojure
* CLJS-1293: Warning settings not conveyed via REPL
* CLJS-1291: pprint whitespace/letter checks are incomplete
* CLJS-1288: compiler doesn't emit "goog.require" for foreign library when optimization level is not set
* check that we actually read something in cjls.repl.server/read-request
* clarify cljs.test/run-tests docstring
* CLJS-1285: load-file regression
* CLJS-1284: IndexedSeq -seq implementation incorrect for i >= alength of internal array
* finish CLJS-1176, remove stray .isAlive method call
* add zero arity `newline` to match Clojure
* CLJS-1206: Images in HTML don't show up when served from localhost:9000
* CLJS-1272: :include-macros description inaccurate in require
* CLJS-1275: Corrected :test-paths in project.clj
* CLJS-1270: Docstring for delay not printed by cljs.repl/doc
* CLJS-1268: cljc support for cljs.closure/compile-file
* CLJS-1269: realized? docstring refers to promise and future
* match Clojure behavior for get on string / array. Need to coerce key into int.
* CLJS-1263: :libs regression, can no longer specify specific files
* CLJS-1209: Reduce produces additional final nil when used w/ eduction
* CLJS-1261: source fn fails for fns with conditional code
