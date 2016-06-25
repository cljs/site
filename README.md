# A website for ClojureScript

- [ ] import homepage (selling points)
- [ ] import book, [ClojureScript Unraveled](https://leanpub.com/clojurescript-unraveled)
- [x] import [API docs](https://github.com/cljs/api)
- [ ] import [cheatsheet](http://cljs.info/cheatsheet)
- [x] create _News_ page (blog posts)
- [x] copy over [_ANN ClojureScript_] release emails to _News_
- [ ] REPL page [using replumb](http://clojurescript.io/), used by [different projects](https://github.com/Lambda-X/replumb#community)

[_ANN ClojureScript_]:https://groups.google.com/forum/#!topicsearchin/clojurescript/%22the$20Clojure$20compiler$20that$20emits$20JavaScript$20source$20code%22

## Development

This is a static site generator written in CLJS, running on Node.

Prerequisites:

- [Node](https://nodejs.org/)
- [npm-cljs](https://github.com/shaunlebron/npm-cljs) (custom)

Build:

```
$ npm install
```

Static server:

```
$ cljs build server
$ node target/server.js
```

Site generator:

```
$ cljs watch sitegen
$ node target/sitegen.js
```

To publish the site to the [GitHub Pages deployment repo](https://github.com/cljs/cljs.github.io):

```
$ ./publish.sh
```
