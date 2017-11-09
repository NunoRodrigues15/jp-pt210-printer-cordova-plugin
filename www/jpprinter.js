var exec = require('cordova/exec');

exports.IO  = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'JpPrinter', 'IO ', []);
};

exports.Label1  = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'JpPrinter', 'Label1 ', []);
};

exports.BLEPrinting = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'JpPrinter', 'BLEPrinting ', []);
};

exports.BTPrinting  = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'JpPrinter', 'BTPrinting ', []);
};
