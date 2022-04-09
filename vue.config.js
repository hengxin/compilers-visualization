/* eslint-disable @typescript-eslint/no-var-requires */
const nested = require("postcss-nested");

module.exports = {
  transpileDependencies: true,
  devServer: {
    port: 8099,
    https: false,
    proxy: null
  },
  outputDir: "output",
  css: {
    loaderOptions: {
      postcss: {
        postcssOptions: {
          plugins: [nested()]
        }
      }
    }
  },
  chainWebpack: config => {
    config.entry("app").clear().add("./src/main.js");
  }
};
