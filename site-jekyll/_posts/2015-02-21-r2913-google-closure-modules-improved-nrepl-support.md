---
layout: post
release_version: "0.0-2913"
title:  "r0.0-2913 - Google Closure Modules, improved nREPL support"
google_group_ann: "clojurescript/n_8WHnlcOGI/1kmATGABVi0J"
---

This release comes with two very big enhancements.

The first is support for Google Closure Modules via the :modules build
option. Google Closure Modules permits splitting advanced compiled
builds into optimal smaller pieces for faster page
loads. ClojureScript's Google Closure Module support is fully
:foreign-libs aware. Source mapping for modules is also fully
supported.

The feature is described in more detail here:
https://github.com/clojure/clojurescript/wiki/Compiler-Options#modules

The second big change is a fundamental rearchitecting of ClojureScript
REPLs. ClojureScript REPLs now support a set of options similar to
those taken by `clojure.main/repl` with small changes to account for
different JavaScript evaluation environments. Many third party REPLs
like Figwheel, Weasel, and Ambly are either unaffected or have already
accounted for these changes. However current tooling leveraging
Piggieback will likely present an inferior experience as Piggieback was
designed to work around the previous limitations of ClojureScript
REPLs. Now that ClojureScript REPLs are more like the standard Clojure
REPL it should be far simpler to add proper interruptible-eval and
load-file nREPL middleware so that existing tooling around nREPL can
more easily integrate ClojureScript REPLs as first class citizens.

Feedback on both of these enhancements is very welcome!

There are also many smaller fixes around REPL command line behavior,
the Nashorn REPL, :foreign-libs resource finding issues, the full
list follows:

## 0.0-2913
* Support custom :output-to for :cljs-base module

## 0.0-2911

### Enhancements
* CLJS-1042: Google Closure Modules :source-map support
* CLJS-1041: Google Closure Modules :foreign-libs support
* Google Closure Modules support via :modules
* CLJS-1040: Source-mapped script stack frames for the Nashorn repl

### Changes
* CLJS-960: On carriage return REPLs should always show new REPL prompt
* CLJS-941: Warn when a symbol is defined multiple times in a file
* REPLs now support parameterization a la clojure.main/repl
* all REPLs analyze cljs.core before entering loop
* can emit :closure-source-map option for preserving JS->JS map
* REPLs can now merge new REPL/compiler options via -setup

### Fixes
* CLJS-998: Nashorn REPL does not support require special fn
* CLJS-1052: Cannot require ns from within the ns at the REPL for reloading purposes
* CLJS-975: preserve :reload & :reload-all in ns macro sugar
* CLJS-1039: Under Emacs source directory watching triggers spurious recompilation
* CLJS-1046: static vars do not respect user compile time metadata
* CLJS-989: ClojureScript REPL loops on EOF signal
* fix DCE regression for trivial programs
* CLJS-1036: use getResources not findResources in get-upstream-deps*
