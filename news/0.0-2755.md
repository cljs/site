
([original Mailing List post](https://groups.google.com/d/msg/clojurescript/FoiqNV5nunQ/xoZ9mD94hpAJ))

Leiningen dependency information:

    [org.clojure/clojurescript "0.0-2755"]

This release fixes regressions to the browser REPL that resulted
from the foreign dependencies enhancement.

This release also includes two very big enhancements. The first
is around macros. If your library does the following:

    (ns foo.bar
       (:require-macros foo.bar))

Then users that require your library:

    (ns bar.baz
      (:require [foo.bar :as foo]))

Will get  `foo.bar` macros automatically required and aliased
to `foo`.

The other big enhancement is true incremental compilation
support. While we have had incremental compilation for some time
it has suffered from the fact that we did not recompile dependent
namespaces - this meant no compilation warnings and corrupted builds.

With auto recompilation of dependent namespaces we eliminate
many cases that previously would have required a clean.

Feedback welcome!

### Enhancements
* CLJS-948: simplify macro usage

### Fixes
* CLJS-927: real incremental compilation
* Browser REPL regressions
* CLJS-991: Wrong inference - inconsistent behavior?
* CLJS-993: binding macro returns non-nil with empty body
* CLJS-972: Node.js REPL eats errors in required ns when using require
* CLJS-986: Add :target to the list of build options that should trigger recompilation
* CLJS-976: Node REPL breaks from uncaught exceptions