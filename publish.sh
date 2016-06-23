#!/bin/bash

set -ex

deploy_dir=deployed

if [ ! -d "$deploy_dir" ]; then
  git clone git@github.com:cljs/cljs.github.io.git $deploy_dir
fi

rm -rf $deploy_dir/*
cp -r output/* $deploy_dir

# Commit and push
pushd $deploy_dir
git add -u
git add .
git commit -m "manual update from publish.sh"
git push origin master
popd
