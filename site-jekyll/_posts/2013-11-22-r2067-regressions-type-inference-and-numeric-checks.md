---
layout: post
release_version: "0.0-2067"
title: "r0.0-2067 - regressions, type inference & numeric checks"
google_group_msg: 'clojurescript/CcIFD5q9kqg/TNJMLdLRCkAJ'
---

This release fixes issues introduced by the source map checks, the
checks have been relaxed.

This release also include a fairly significant enhancement - pervasive
simple type inference. This is to detect common mistakes involving
primitive arithmetic and non-numeric types. However this also adds a
new pass to the compiler and stores more information in compiler
environment. Feedback on signifiant changes to compile time and memory 
usage welcome.

Enhancements:

* pervasive inference, inlined primitive arithmetic is now checked

Bug fixes:

* CLJS-685: elide :end-column :end-line from metadata
* CLJS-694: remove Java 7 dependency
