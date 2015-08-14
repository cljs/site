---
layout: post
release_version: "0.0-2014"
title: "r0.0-2014 - source maps, incremental compilation, and internal changes"
google_group_ann: 'isf7k35pThA/CXyJr4v4seYJ'
---

There are a number of significant enhancements in this
release. We finally have relative source maps! This should be big
for people integrating ClojureScript with existing web
based workflows.

Under the hood Chas Emerick has improved how the analyzer works making
it thread safe. This make the compiler considerably more robust and
eliminates some race conditions in the browser REPL support.

Incremental compilation is enhanced both with and without source
maps. In particular we now tag ClojureScript compiled JavaScript files
with the version of the compiler used - this should make transitioning
to a new version of the compiler considerably less frustrating - stale
files will get compiled.

For those people using the compiler internals directly you will likely
encounter breakage. If anyone feels inclined to outline a more stable
interface to internals please get involved in leading an incremental
process towards a stable and flexible API for tool builders.

Enhancements:

* relative source map paths, all original sources will be copied to
  :ouput-dir this should make integrating with web workflow much simpler.
* runtime obtainable compiler version number, *clojurescript-version* now
  available at runtime as a string

Bug fixes:

* CLJS-643: make the ClojureScript compiler (more) idempotent
* CLJS-662: CLJS files compiled from JARs get lost from source map
* CLJS-661: (try ... (catch :default e ...) ...)
* CLJS-627: Add warnings on function arity problems
* CLJS-654: Quit repljs on ^D, don't loop on nil
* CLJS-659: tag compiled files with compiler version
* CLJS-642: deftype/record should not emit goog.provide
* CLJS-648: persistent assoc/conj on a transient-created collision node
* CLJS-631: Use ana/namespaces for shadowing vars
* CLJS-641: js* overflow for large inputs
* CLJS-645: parse-ns needs to include 'constants-table as a dep
* CLJS-646: single segment namespaces and reify don't work
* CLJS-521: pass along entire repl environment when loading dependencies
