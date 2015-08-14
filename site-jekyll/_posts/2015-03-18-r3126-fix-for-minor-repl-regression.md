---
layout: post
title:  "r0.0-3126 - fix for minor REPL regression"
---

([original Mailing List post](https://groups.google.com/d/msg/clojurescript/rt7Fc86v1aU/u5G56vi5M6sJ))

Leiningen dependency information:

    [org.clojure/clojurescript "0.0-3126"]

This release just fixes a minor REPL related regression.

## 0.0-3126

### Fixes
* Need to wrap REPL -setup calls in cljs.compiler/with-core-cljs
