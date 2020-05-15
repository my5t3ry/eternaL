export const promise = new Promise(function (resolve, reject) {
  setTimeout(function () {
    resolve('Promise returns after 1.5 second!');
  }, 2000);
});

