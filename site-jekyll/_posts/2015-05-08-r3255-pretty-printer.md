---
layout: post
release_version: "0.0-3255"
title:  "r0.0-3255 - Pretty Printer and Latest Closure Compiler/Library"
author: "David Nolen"
google_group_msg: "clojurescript/A--qv0JxfO8/FoCzLNQ-D4EJ"
---

A big thanks goes out to Jonathan Boston and Shaun Lebron for this
release. Thanks to their efforts ClojureScript now includes a full
port of clojure.pprint under the cljs.pprint namespace. This was the
last major namespace in need of porting to ClojureScript.

The release also bumps several dependencies: Clojure 1.7.0-beta2,
tools.reader 0.9.2, Closure Compiler v20150505, and Closure Library
0.0-20150505-021ed5b3.

This release also fixes some regressions around async testing,
docstring REPL support, arglist meta, and more.

As always feedback welcome!

## 0.0-3255

### Changes
* Update Closure Library dependency
* CLJS-1252: Update Closure Compiler Dependency to v20150505
* .clj -> .cljc for important analysis / compilation bits
* add public cljs.compiler.api namespace
* CLJS-1224: cljs.repl: Memoize stack frame mapping
* depend on tools.reader 0.9.2

### Enhancements
* add cljs.pprint/pp macro
* CLJS-710: port clojure.pprint
* CLJS-1178: Compiler does not know Math ns is not not-native
* add getBasis methods to deftype and defrecord ctors a la Clojure JVM
* support ^long and ^double type hints

### Fixes
* fix cljs-1198 async testing regression
* CLJS-1254: Update REPL browser agent detection CLJS-1253: Create/Use
  new Closure Library Release
* CLJS-1225: Variadic function with same name as parent function gives
  runtime error in advanced compile mode.
* CLJS-1246: Add cljs.core/record? predicate.
* CLJS-1239: Make eduction variadic.
* CLJS-1244: tagged-literal precondition check missing wrapping vector
* CLJS-1243: Add TaggedLiteral type & related fns
* CLJS-1240: Add cljs.core/var?
* CLJS-1214: :arglists meta has needless quoting CLJS-1232: bad
  arglists for doc, regression
* CLJS-1212: Error in set ctor for > 8-entry map literal
* CLJS-1218: Syntax quoting an alias created with :require-macros
  throws ClassCastException
* CLJS-1213: cljs.analyzer incorrectly marks all defs as tests when
  eliding test metadata
* CLJS-742: Compilation with :output-file option set fails
