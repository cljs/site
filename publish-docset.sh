#!/bin/bash

set -ex

# Docset versions have two parts:
#
# 1.10.516/2
# ^^^^^^^^   CLJS_VERSION
#          ^ DOCSET_TAG

# pull version from cljs-api.edn
CLJS_VERSION=$(grep -m 1 ":version " cljs-api.edn | cut -d'"' -f 2 | tr -d '+')

# USER CAN PASS DOCSET TAG AS FIRST ARG (defaults to 1)
DOCSET_TAG="1"
if [ ! -z "$1" ]; then
  DOCSET_TAG="$1"
fi

echo "Creating Docset for ClojureScript $CLJS_VERSION/$DOCSET_TAG"

# Go into docset workspace
cd docset
repo=Dash-User-Contributions
rm -rf $repo
git clone git@github.com:shaunlebron/${repo}.git
cd $repo
git remote add upstream git@github.com:Kapeli/${repo}.git

# reset unsaved changes and go to master branch
git reset --hard
git checkout master

# remove cljs branch
git branch -D cljs || echo;

# Need to reset to make simple squashed PRs and to prevent possible problems
# from Dash's history rewriting from large file removal.
#    source: https://gist.github.com/glennblock/1974465
git fetch upstream
git reset --hard upstream/master

# create PR branch
git checkout -b cljs upstream/master
echo "PR branch 'cljs' created."

# add docset file
cp ../ClojureScript.tgz docsets/ClojureScript/
echo "latest docset copied."

# updating version in json
sed -i.bak -e "s|\"version\":.*|\"version\": \"$CLJS_VERSION/$DOCSET_TAG\",|" docsets/ClojureScript/docset.json
rm docsets/ClojureScript/docset.json.bak

git add docsets/ClojureScript/
git commit -m "ClojureScript $CLJS_VERSION"
git push origin HEAD -f

open "https://github.com/shaunlebron/Dash-User-Contributions/compare/cljs?expand=1"
