var cryptoJS = require("crypto-js");
var B = "hj6cdzrhj72x8ht1";
function decrypt(response) {
    var e = response.slice(16);
    var a = response.slice(0, 16);
    return JSON.parse(cryptoJS.AES.decrypt(e, cryptoJS.enc.Utf8.parse(a), {
        iv: cryptoJS.enc.Utf8.parse(B),
        mode: cryptoJS.mode.CBC
    }).toString(cryptoJS.enc.Utf8));
}