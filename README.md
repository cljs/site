# A website for ClojureScript

> __NOTE__: rebuilding the work at [cljs.info](http://github.com/cljsinfo/cljs.info) using a simpler content system to encourage more collaboration and content.

![screenshot](http://i.imgur.com/rInPHTP.png)

## Roadmap

- [ ] import homepage (selling points)
- [ ] import book, [ClojureScript Unraveled](https://leanpub.com/clojurescript-unraveled)
- [ ] import [API/Syntax docs](https://github.com/cljsinfo/cljs-api-docs)
- [ ] import [cheatsheet](http://cljs.info/cheatsheet)
- [x] create _News_ page (blog posts)
- [x] copy over [_ANN ClojureScript_] release emails to _News_

[_ANN ClojureScript_]:https://groups.google.com/forum/#!topicsearchin/clojurescript/%22the$20Clojure$20compiler$20that$20emits$20JavaScript$20source$20code%22

###### On the horizon (owners needed)

- [ ] [REPL page](http://chimeces.com/cljs-browser-repl/)

## Tech Rationale

We want to simplify content management, collaboration, and deployment for this
website as much as possible so it can stay around, be adapted, and change hands
for as long as it needs to.  Thus, we are using a Jekyll site deployed to
GitHub pages, with some ClojureScript for future dynamic parts of course!

### Instructions

I couldn't find a static site generator as good as Jekyll, so we gotta use Ruby!
We're using it with [bundler](http://bundler.io) to have deterministic dependencies.

```shell
# setup
$ cd site-jekyll
$ gem install bundler
$ bundle install

# start the website
$ bundle exec jekyll serve
```

Anyone with push access to the [GitHub Pages deployment repo] can update the live website:

```
$ ./publish.sh
```

[GitHub Pages deployment repo]:https://github.com/cljsinfo/cljsinfo.github.io
