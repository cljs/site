#!/bin/sh

# Planck allows us to run tasks written in ClojureScript,
# which we use to generate static site pages.
# We can eventually replace this

# Cache the classpath so boot doesn't slow down our build.
# This allows Planck to use dependencies.
if [ ! -f .classpath ]; then
  classpath=`boot show -c | tee .classpath`
else
  classpath=`cat .classpath`
fi

planck \
  -c src:$classpath \
  -m sitegen.core
