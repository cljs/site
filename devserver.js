const express = require('express');
const fs = require('fs');

const root = __dirname + '/output';
const port = 3795;
const server = express();

function canAppendHtmlExt(path) {
  const file = root + path + '.html';
  try {
    fs.accessSync(file, fs.F_OK);
    return true;
  } catch (e) {
    return false;
  }
}

server.use(function(req, res, next) {
  if (canAppendHtmlExt(req.path)) {
    req.url += '.html';
  }
  next();
});
server.use(express.static(root));
server.listen(port, function() {
  console.log('server listening on port ' + port);
});
