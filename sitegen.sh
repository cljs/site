#!/bin/bash

compiled=target/main.js

for f in `find src`; do
  if [ "$f" -nt "$compiled" ]; then
    echo "Detected changed cljs file.  Compiling sitegen..."
    boot sitegen
    break
  fi
done

node $compiled
