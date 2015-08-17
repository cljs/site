#!/bin/bash

set -ex

deploy_dir=deployed

if [ ! -d "$deploy_dir" ]; then
  git clone git@github.com:cljsinfo/cljsinfo.github.io.git $deploy_dir
fi

# Build the site, and dump into deploy directory.
# (NOTE: this clears the entire contents of the deploy directory first!
#        but it keeps the `keep_files` entries listed in `site-jekyll/_config.yml`.)
pushd site-jekyll
bundle exec jekyll build --destination ../$deploy_dir
popd

# Commit and push
pushd $deploy_dir
git add -u
git add .
git commit -m "manual update from publish.sh"
git push origin master
popd
