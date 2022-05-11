# ClojureScript API docs site

Hosted at <http://cljs.github.io/api/>.

## Development

This is a static site generator using [clj].

Run:

```
clj -Mrun
```

To publish the site to the [GitHub Pages deployment repo](https://github.com/cljs/cljs.github.io):

```
./publish-site
```

To publish the docset to [Dash], run the following:

```
./publish-docset
```

## Known issues

These are outstanding issues leftover from porting the site generator from node.js to clojure:

- Markdown link references aren’t processed by clj-markdown
- Syntax not highlighted

[Dash]:https://kapeli.com/dash
[clj]:https://clojure.org/guides/getting_started
