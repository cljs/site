# A site for cljs

> __NOTE__: this is an experiment to rebuild the work at [cljs.info](http://github.com/cljsinfo/cljs.info) using a simpler content system to encourage more collaboration and content.

## Roadmap

- [ ] import homepage (selling points)
- [ ] import asciidoc book, [ClojureScript Unraveled](https://leanpub.com/clojurescript-unraveled)
- [ ] import [API/Syntax docs](https://github.com/cljsinfo/cljs-api-docs)
- [ ] import [cheatsheet](http://cljs.info/cheatsheet)
- [x] create _News_ page (blog posts)
- [x] copy over [_ANN ClojureScript_] release emails to _News_

[_ANN ClojureScript_]:https://groups.google.com/forum/#!topicsearchin/clojurescript/%22the$20Clojure$20compiler$20that$20emits$20JavaScript$20source$20code%22

###### On the horizon (owners needed)

- [ ] [REPL page](http://chimeces.com/cljs-browser-repl/)

## Instructions

I couldn't find a static site generator as good as Jekyll, so we gotta use Ruby!
We're using it with [bundler](http://bundler.io) to have deterministic dependencies.

```shell
# setup
$ cd site-jekyll
$ gem install bundle
$ bundle install

# start the website
$ bundle exec jekyll serve
```
