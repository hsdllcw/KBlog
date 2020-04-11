const webpack = require('webpack');
const path = require('path');
const webpackConfig = require('./webpack.base.conf');
const merge = require('webpack-merge');
const fs = require('fs');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
let server =  JSON.parse(fs.readFileSync('./build/server.json', 'utf8'));
module.exports = merge(webpackConfig, {
    mode: "development",
    //插件
    plugins: [
        new MiniCssExtractPlugin({
            filename: `assets/css/[name].css`
        }),
        new webpack.HotModuleReplacementPlugin()
    ],
    devtool: 'inline-source-map',
    devServer: {
        contentBase: path.resolve(__dirname, 'target'), //最好设置成绝对路径
        historyApiFallback: false,
        hot: true,
        inline: true,
        stats: 'errors-only',
        host: server.host,
        port: server.port,
        disableHostCheck: true,
        overlay: true,
        open: true,
        proxy: {
            "/api": {
                target: 'http://localhost:8080',
                pathRewrite: {'^/api': ''},
                changeOrigin: true
            }
        },
    }
});