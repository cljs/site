
The primary reason for this release is a critical hashing bug 
in Safari clients that surfaced due to the Murmur3 work. 

### Fixes 
* CLJS-839: Mobile Safari Math.imul issue 
* CLJS-845: incorrect behavior of `sequence` when given multiple collections 
* count check in equiv-sequential if both arguments are ICounted 
* only keep the param names when storing :method-params instead of the 
  entire param AST 
* preserve var metadata for deftype* and defrecord* 
* preserve var metadata when creating deftype/record factory fns 
* CLJS-831: Extending EventType to js/Element breaks Nashorn 