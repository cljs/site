---
layout: post
release_version: "0.0-2814"
title:  "r0.0-2814 - Nashorn REPL, async testing, and much more"
google_group_msg: "clojurescript/KTOhX-QvpRo/j9-st6WUnI8J"
---

There are numerous enhancements in this release including: a Nashorn
REPL, Node.js 0.12 support, cljs.test async testing support,
`cljs.closure/watch`, extra JSDoc annotation support, unified source
mapping on client/server (thus REPLs!), and many small fixes.

I'm particularly excited about unified source mapping as this means we
get a much better debugging experience on newer targets (for us) like iOS, see https://github.com/omcljs/ambly

## 0.0-2814

### Enhancements
* add simple source directory `cljs.closure/watch` watcher using java.nio
* CLJS-1022: Concatenate foreign dependencies safely
* CLJS-988: Support async testing in cljs.test
* CLJS-1018: Add support for cljs.core/*e Modify the JavaScript that is sent for evaluation to wrap in a try and then catch any exception thrown, assign it to *e, and then rethrow.
* CLJS-1012: Correct behavior when *print-length* is set to 0
* Added new :closure-extra-annotations compiler option allowing to define extra JSDoc annotation used by closure libraries.
* Mirrored source map support APIs on server/client
* Unified source mapping support in REPLs
* Nashorn REPL (thanks Pieter van Prooijen)

### Fixes
* CLJS-1023: regression, macro-autoload-ns? and ns-dependents need to throw on cyclic dependencies
* fix require with browser REPL, set base path to "goog/"
* CLJS-1020: off by one error in REPL source map support
* Node.js 0.12 support
* browser REPL needs to respect :output-dir
* CLJS-1006: Implicit dependency of clojure.browser.repl on cljs.repl
* CLJS-1005: Browser REPL creates 'out' directory no matter what
* CLJS-1003: fix cljs.test run-tests do-report :summary issues
* CLJS-1003: Cannot pass custom env to run-tests
* Windows Node.js REPL issues
