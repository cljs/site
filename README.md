# ClojureScript site

Hosted at <http://cljs.github.io/api/>.

[clojurescript.org](https://clojurescript.org/) now exists, but this is here mainly for:
- API docs (for website and Dash)
- release post index (announcements from mailing list)

In the future, I'd like to see this integrated into the official site.

## Development

This is a static site generator written in CLJS, running on Node.

Prerequisites:

- [Clojure](https://clojure.org/guides/getting_started)
- [Node](https://nodejs.org/)

Fetch depencies:

```
$ npm install
```

Static server:

```
$ ./build-server
$ npm run server
```

Site and Docset generator:

```
$ ./build-client
$ ./build-sitegen
$ npm run sitegen
```

To publish the site to the [GitHub Pages deployment repo](https://github.com/cljs/cljs.github.io):

```
$ ./publish.sh
```

To publish the docset to [Dash], run the following:

```
$ cd docset
$ ./docset-pr.sh
```

[Dash]:https://kapeli.com/dash
