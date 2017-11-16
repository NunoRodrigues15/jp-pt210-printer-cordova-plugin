var exec = require('cordova/exec');

var PLUGIN_NAME = 'JpPrinter';

var JpPrinter = {
  echo: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'echo', [phrase]);
  },
  getDate: function(cb) {
    exec(cb, null, PLUGIN_NAME, 'getDate', []);
  }
  // IO : function(successCallback, errorCallback) {
  //     cordova.exec(successCallback, errorCallback, PLUGIN_NAME, 'IO ', []);
  // },
  //
  // Label1 : function(successCallback, errorCallback) {
  //     cordova.exec(successCallback, errorCallback, PLUGIN_NAME, 'Label1 ', []);
  // },
  //
  // BLEPrinting : function(successCallback, errorCallback) {
  //     cordova.exec(successCallback, errorCallback, PLUGIN_NAME, 'BLEPrinting ', []);
  // },
  //
  // BTPrinting : function(successCallback, errorCallback) {
  //     cordova.exec(successCallback, errorCallback, PLUGIN_NAME, 'BTPrinting ', []);
  // }
}

module.exports = JpPrinter;
