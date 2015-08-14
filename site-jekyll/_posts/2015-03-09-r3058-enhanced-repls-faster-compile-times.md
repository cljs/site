---
layout: post
title:  "r0.0-3058 - Enhanced REPLs, faster compile times"
---

([original Mailing List post](https://groups.google.com/d/msg/clojurescript/fdT3f1HxJzM/rCbi7L1AI24J))

Leiningen dependency information:

    [org.clojure/clojurescript "0.0-3058"]

This is a significant enhancement release around REPLs and compile times.

All builtin REPLs (Nashorn, Node.js, Rhino and the browser REPL) now
support the helper functions normally available via clojure.repl,
these include: doc, find-doc, apropos, dir, source, and pst.  All of the
builtins REPL now also support source mapped stacktraces.

This release also includes many enhancements around compile times.

ClojureScript now ships with a default :optimizations setting of
:none. Implicit now when using :none is source map generation and analysis
caching. Analysis caching significantly speeds up compile times.

The standard library (cljs.core) is now AOTed compiled to JavaScript
along with an AOTed analysis dump and an AOTed source map. This
dramatically cuts down on cold start compile times. The standard
library is never actually ever analyzed or compiled in your own
builds. The result is particularly dramatic for REPLs.

ClojureScript is also now available for the first time as a standalone
AOTed JAR. The Quick Start introduction has been rewritten in terms of
the standalone JAR:
https://github.com/clojure/clojurescript/wiki/Quick-Start

The new Quick Start is essential reading even if you are a relatively
experienced ClojureScript developer.

As usual feedback welcome!

## 0.0-3058

### Enhancements
* browser REPL source mapping for Firefox, Safari, Chrome
* macro support in REPL special functions
* CLJS-897: AOT core.cljs CLJS-899: AOT cache core.cljs analysis
* CLJS-1078: Nashorn REPL should use persistent code cache
* CLJS-1079: add way to execute arbitrary fn upon watch build completion
* CLJS-1034: Support REPL-defined functions in stacktrace infrastructure
* source mapping for Rhino
* CLJS-1071: support symbol keys in :closure-defines
* CLJS-1014: Support Closure Defines under :none
* CLJS-1068: node target define
* CLJS-1069: Generic :jsdoc support
* CLJS-1030: add `cljs.repl/pst`
* add `cljs.repl/apropos`, `cljs.repl/find-doc`, `cljs.repl/dir`
* fix `cljs.analyzer.api/all-ns` docstring
* add `cljs.analyzer.api/ns-publics`
* CLJS-1055: cljs.repl/doc should support namespaces and special forms
* Add ClojureScript special form doc map
* CLJS-1054: add clojure.repl/source functionality to cljs.repl
* CLJS-1053: REPLs need import special fn

### Changes
* move :init up in cljs.repl/repl
* CLJS-1087: with-out-str unexpectedly affected by *print-newline*
* CLJS-1093: Better compiler defaults
* Bump deps latest Closure Compiler, Rhino 1.7R5, data.json 0.2.6, tool.reader 0.8.16
* more sensible error if cljs.repl/repl arguments after the first incorrectly supplied
* default REPLs to :cache-analysis true
* default :output-dir for Nashorn and Node REPLs
* change ES6 Map `get` support to take additional `not-found` parameter
* deprecate clojure.reflect namespace now that REPLs are significantly enhanced, static vars, etc.

### Fixes
* stop blowing away cljs.user in browser REPL so REPL fns/macros remain available
* CLJS-1098: Browser REPL needs to support :reload and :reload-all
* CLJS-1097: source map url for AOTed cljs.core is wrong
* CLJS-1094: read option not used by cljs.repl/repl*
* CLJS-1089: AOT analysis cache has bad :file paths
* CLJS-1057: Nashorn REPL should not use EDN rep for errors
* CLJS-1086: Keyword constants should have stable names
* CLJS-964: Redefining exists? does not emit a warning like redefining array? does.
* CLJS-937: local fn name should be lexically munged
* CLJS-1082: analysis memoization bug
* CLJS-978: Analysis caching doesn't account for constants table
* CLJS-865: remove `cljs.js-deps/goog-resource` hack
* CLJS-1077: analyze-deps infinite recursive loop
* manually set *e in Rhino on JS exception
* REPL options merging needs to be more disciplined
* CLJS-1072: Calling .hasOwnProperty("source") in Clojurescript's string/replace will break with ES6
* CLJS-1064: ex-info is not printable
* Fix REPLs emitting code into .repl directory
* CLJS-1066: Rhino REPL regression
* be more disciplined about ints in murmur3 code
* Node.js REPL should work even if :output-dir not supplied
* Nashorn environment doesn't supply console, setup printing correctly
