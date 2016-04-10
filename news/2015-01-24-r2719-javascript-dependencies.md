---
release_version: '0.0-2719'
title:  "r0.0-2719 - JavaScript Dependencies"
author: "David Nolen"
google_group_msg: 'clojurescript/pJ_EYHkYAUs/mLi8XfiQxZsJ'
---

ClojureScript is not an island, like Clojure on the JVM, ClojureScript
embraces the many benefits provided by the host. However this goal
has been hampered by another goal - the compilation stragey. Google
Closure Compiler offers superior optimization and minification for
ClojureScript while simultaneously making it considerably more
difficult to integrate non-Closure compatible libraries. Using popular
libraries like jQuery, React or D3 is an error prone process: "which
extern did I forget?", "which script tag did I miss for development?",
"are these script tags in dependency order?".

No more. 0.0-2719 delivers full support for non-Closure compatible
libraries through some less known features that have been lurking
around for almost three years - `deps.cljs` and the :foreign-libs
compiler option.

`deps.cljs` is a simple EDN file provided at the root of a JAR that
describes additional build information for the ClojureScript
compiler. For example here is the `deps.cljs` for the React JAR I
maintain:

{
 :foreign-libs [{:file     "react/react.js"
                 :file-min "react/react.min.js"
                 :provides ["com.facebook.React"]}
                {:file     "react/react_with_addons.js"
                 :file-min "react/react_with_addons.min.js"
                 :provides ["com.facebook.ReactWithAddons"]}]
 :externs ["react/externs/react.js"]
}

This file provides all the information ClojureScript needs to
correctly manage the foreign dependency for you under all compilation
modes and REPLs.

In a REPL:

   cljs.user> (require 'com.facebook.React)
   cljs.user> (. js/React 
                (renderToString 
                  (. js/React (DOM.div nil "Hello!"))))

In your project:

   (ns foo.bar
     (:require com.facebook.React))

   (enable-console-print!)

   (println
     (. js/React
       (renderToString
         (. js/React (DOM.div nil "Hello!")))))

The above works under all compilation modes. There is no need to
include React as a script tag under development, there is no need to put
script tags in dependency order, there is no need to add React to
:preamble under :advanced, and there is no need to explicitly provide
:externs.

All that is required is that JavaScript libraries be packaged in JARs
with a `deps.cljs`.

Some of you might reasonably ask why not a tool like Bower for this
instead?  Bower requires an additional dependency on Node.js. While
ClojureScript embraces Node.js as a useful target it is not a
requirement to be productive. Bower manages dependencies, but in the
Clojure world we have already embraced Maven for this task and have
done the same for ClojureScript. Finally Bower does not address the
problem of loading libraries at runtime. This is challenging to do -
some JavaScript libraries adopt CommonJS, some AMD, and the most
popular ones make no assumptions at all allowing users to simply use
script tags as they have done for nearly two decades. The above
solution addresses the issue for all ClojureScript users by
eliminating paralysis of choice. Finally none of the above
precludes Bower usage in any way whatsoever.

This new feature addresses a long outstanding pain point with
ClojureScript development. All that is required is that we take the
time to properly package up the most popular JavaScript libraries that
fill gaps not currently served by exising ClojureScript and Google
Closure Library functionality.

Please kick the tires and feedback is most welcome. This feature
touched many bits of code so there are likely to be wrinkles and we want
to get these ironed out as quickly as possible.

## 0.0-2719

### Changes
* Full support for foreign dependencies
* CLJS-985: make ex-info not lose stack information
* CLJS-984: Update Node.js REPL support to use public API
* CLJS-963: do not bother computing goog/dep.js under :none

### Fixes
* CLJS-982: Var derefing should respect Clojure semantics
* CLJS-980: ClojureScript REPL stacktraces overrun prompt in many cases
* CLJS-979: ClojureScript REPL needs error handling for the special functions
* CLJS-971: :reload should work for require-macros special fn
* CLJS-980: ClojureScript REPL stacktraces overrun prompt in many cases
* CLJS-979: ClojureScript REPL needs error handling for the special functions
* CLJS-971: :reload should work for require-macros special fn
* CLJS-936: Multi arity bitwise operators
* CLJS-962: fix inconsistent hashing of empty collections
