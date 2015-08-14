---
layout: post
release_version: "0.0-2843"
title:  "r0.0-2843 - Node, Node, Node"
google_group_ann: 9ZrGYIhRzqM/-6GM31HTz7wJ
---

This release is primarily about outstanding Node.js target
issues. Further changes have been made to support Node.js v0.12
specifically around the deprecation of util.print. Node.js target now
supports :main same as browser based :none builds. Node.js :simple and
:advanced builds now set goog.global correctly ensuring that
core.async works properly. The Node.js REPL should now work on slower
machines. And all of these enhancements have been made with the
Windows platform in mind.

We've also solidified and documented the new generic source mapping
infrastructure for custom REPLs. Figwheel and Ambly are already taking
advantage of this to great effect.

Printing is now customizable in order to support custom printing in
Chrome DevTools, there's some prototype work based on this happening
here: https://github.com/binaryage/cljs-devtools-sample.

cljs.test now supports macro inference simplifying testing at a REPL.

Full list of changes, fixes, and enhancements follows.

Feedback welcome!

## 0.0-2843

### Enhancements
* CLJS-1032: Node.js target should support :main
* require cljs.test macro ns in cljs.test to get macro inference goodness
* include :url entries to original sources in mapped stacktraces if it can be determined   from the classpath
* support custom mapped stacktrace printing
* provide data oriented stacktrace mapping api
* CLJS-1025: make REPL source mapping infrastructure generic
* CLJS-1010: Printing hook for cljs-devtools
* CLJS-1016: make "..." marker configurable

### Changes
* CLJS-887: browser repl should serve CSS
* CLJS-1031: Get Closure Compiler over https in the bootstrap script

### Fixes
* cljs.nodejscli ns needs to set `goog.global` when `COMPILED` is true, this fixes the fundamental issues for ASYNC-110
* CLJS-967: "java.net.ConnectException: Connection refused" when running node repl
* pass relevant source map options in the incremental compile case
* add some missing source-map customization flags to optimized builds
* fix missed Rhino REPL regression, the surrounding REPL infrastructure creates cljs.user for us
* util.print has been deprecated in Node.js v0.12. Switch to console.log in Node.js REPLs.
* change `cljs.closure/watch` so it correctly watches all subdirectories do not recompile unless changed path is a file with .cljs or .js extension
