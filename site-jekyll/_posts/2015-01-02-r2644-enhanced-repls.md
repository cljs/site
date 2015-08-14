---
layout: post
release_version: '0.0-2644'
title:  "r0.0-2644 - enhanced REPLs"
google_group_ann: 'clojurescript/mRAED4TFlCM/VMi-AOQBzFQJ'
---

This release is one of the most significant in a very long while - it includes 
enhanced REPLs that are far closer to the experience provided by Clojure 
itself. 

All REPLs when supplied with an `:output-dir` & `:cache-analysis true` can 
now boot rapidly - a second or less on newer hardware. 

All REPLs now support `in-ns`, `doc`, and `require`. 

There is a brand new Node.js based REPL that doesn't suffer from the performance 
issues present in Rhino. As to why we didn't go with Nashorn - Nashorn loads 
JavaScript 10X slower than Rhino or Node.js. 

The Node.js REPL ships with source mapping support and correctly handles 
Node.js requires so that you can hot update namespaces as expected. 

There were a large number of changes to enhance the REPL experience so 
there's likely edge cases we've missed - feedback, bug fixes, and further 
enhancement are incredibly welcome. 

For basic instructions on the running the REPLs from a checkout: 

https://github.com/clojure/clojurescript/wiki/Quick-Start#local-clojurescript-repl 

## 0.0-2644 

### Enhancements 
* Add Node.js REPL 
* REPLs can now reuse build/analysis caching 
* in-ns, require, doc support in REPLs 

### Changes 
* add :verbose flag to compiler to output compiler activity 
* add *load-macros* to cljs.analyzer to optionally disable macro loading 
* errors during ns parsing always through 
* `cljs.util/compiled-by-version` needs to always return String 
* pin Closure Compiler in bootstrap script 
* refactor cljs.build.api namespace 

### Fixes 
* CLJS-953: require REPL special fn can only take one argument 
* CLJS-952: Bad type hinting on bit-test 
* CLJS-947: REPL require of goog namespaces does not work 
* CLJS-951: goog.require emitted multiple times under Node.js REPL 
* CLJS-946: goog.require in REPLs will not reload recompiled libs 
* CLJS-950: Revert adding compiled-by string to CLJS deps file 
* CLJS-929: Minor fixes to test script 
* CLJS-946: goog.require in REPLs will not reload recompiled libs 
* add cljs.test/are macro 
* CLJS-931 : cljs.compiler/requires-compilation? ignores changes to 
build options 
* CLJS-943: REPL require special fn is brittle 
* CLJS-941: Warn when a symbol is defined multiple times in a file 
* CLJS-942: Randomized port for Node.js REPL if port not specified 
* CLJS-675: QuickStart example not working properly 
* CLJS-935: script/noderepljs leaves node running after exit 
* CLJS-918: preserve :arglists metadata in analysis cache 
* CLJS-907: False positives from arithmetic checks 
* CLJS-919 compare-and-set! relies on Atom record structure instead of protocols 
* CLJS-920 add-watch/remove-watch should return reference, as in Clojure 
* CLJS-921: cljs.repl/doc output includes namespace twice 
