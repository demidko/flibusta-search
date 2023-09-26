// noinspection JSUnresolvedFunction

const MiniCss = require("mini-css-extract-plugin");
const webpack = require("webpack");
const CopyPlugin = require("copy-webpack-plugin");

module.exports = {
  plugins: [
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
    }),
    new MiniCss(),
    new CopyPlugin({
      patterns: [
        "./src/index.html",
        {
          from: "./src/favicon",
          to: "./favicon/.",
        },
      ],
    }),
  ],
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [MiniCss.loader, "css-loader"],
      },
    ],
  },
  entry: "./src/main.js",
  output: {
    clean: true,
  },
};