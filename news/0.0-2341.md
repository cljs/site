
This releases comes with considerably better analysis. Vars from other 
namespaces are finally also verified. Protocols previously saw very 
little analysis support. Protocol method implementations are now 
checked for validity against the declared protocol. 

Transducers are also now in sync with Clojure 1.7.0-alpha2 

Feedback welcome! 

### Enhancements 
* transducers 

### Fixes 
* CLJS-704: warn if protocol extended to type multiple times in extend-type 
* CLJS-702: warn if protocol doesn't match declared 
* CLJS-859: use https for the bootstrap script 
* CLJS-855: combinatorial code generation under advanced 
* CLJS-858: resolve-existing var does not check vars outside current ns 
* CLJS-852: same group-by as Clojure 
* CLJS-847: Safari toString fix 
* CLJS-846: preserve namespace metadata 