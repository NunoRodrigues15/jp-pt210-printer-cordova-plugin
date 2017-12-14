var exec = require('cordova/exec');

var PLUGIN_NAME = 'JpPrinterPlugin';

var JpPrinterPlugin = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  },
  initBroadcast: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'initBroadcast', []);
  },
  connect: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'connect', []);
  },
  connectTo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'connectTo', [phrase]);
  },
  printText: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'printText', [phrase]);
  },
  disconnect: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'disconnect', []);
  }
};

module.exports = JpPrinterPlugin;
