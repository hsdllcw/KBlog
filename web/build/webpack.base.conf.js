const webpack = require('webpack');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TransferWebpackPlugin = require('transfer-webpack-plugin');
const autoprefixer = require('autoprefixer');
const util = require("./util");

const resolve = (dir) => path.resolve(__dirname, dir);

module.exports = {
    entry: util.getEntry(),
    output: {
        path: path.resolve(__dirname, '../target'),
        filename: `bundle/[name].js`,
        publicPath: "/",
        chunkFilename: 'bundle/chunk.[name].[chunkhash].js'  // 配合懒加载打包重命名
    },
    externals:util.getExternalModules(JSON.parse(JSON.stringify(util.externalConfig))),
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                loader: 'babel-loader',
                // 指定目录去加载babel-loader，提升运行、打包速度
                include: [resolve('../src'), resolve('../node_modules/webpack-dev-server/client')],
                // 排除目录，提升运行、打包速度
                exclude: file => (
                    /node_modules/.test(file)
                )
            },
            {
                test: /\.js$/,
                include: /src/,
                use: [
                    {
                        loader: 'babel-loader'
                    }
                ],
            },
            {test: /\.ts$/, use: 'ts-loader'},
            {
                test: /\.css$/,
                //use:['style-loader','css-loader','postcss-loader']//css不分离写法
                //css分离写法
                use: [MiniCssExtractPlugin.loader, "css-loader", {
                    loader: "postcss-loader",
                    options: {
                        plugins: [
                            autoprefixer({
                                browsers: ['ie >= 8', 'Firefox >= 20', 'Safari >= 5', 'Android >= 4', 'Ios >= 6', 'last 4 version']
                            })
                        ]
                    }
                }]
            },
            {
                test: /\.scss$/,
                //use:['style-loader','css-loader','sass-loader','postcss-loader']//css不分离写法
                //css分离写法
                use: ['style-loader', "css-loader", {
                    loader: "postcss-loader",
                    options: {
                        plugins: [
                            autoprefixer({
                                browsers: ['ie >= 8', 'Firefox >= 20', 'Safari >= 5', 'Android >= 4', 'Ios >= 6', 'last 4 version']
                            })
                        ]
                    }
                }, "sass-loader"]
            },
            {
                test: /\.(png|jpg|gif|jpeg)$/,
                use: [{
                    loader: "file-loader",
                    options: {
                        name: "[name].[ext]",
                        publicPath: "/assets/img",//以网站根路径（target目录）为准
                        outputPath: "assets/img"//target下的目录
                    }
                }]
            },
            {
                test: /\.hbs$/,
                loader: 'handlebars-loader'
            },
            {
                test: /\.eot(\?v=\d+\\d+\\d+)?$/,
                use: [{
                    loader: "file-loader",
                    options: {
                        name: "[name].[ext]",
                        publicPath: "/assets/fonts",//以网站根路径（target目录）为准
                        outputPath: "assets/fonts"//target下的目录
                    }
                }]
            },
            {
                test: /\.(woff|woff2)$/,
                use: [{
                    loader: "url-loader?prefix=font/&limit=5000",
                    options: {
                        name: "[name].[ext]",
                        publicPath: "/assets/fonts",//以网站根路径（target目录）为准
                        outputPath: "assets/fonts"//target下的目录
                    }
                }]
            },

            {
                test: /\.ttf(\?v=\d+\\d+\\d+)?$/,
                use: [{
                    loader: "url-loader?limit=10000&mimetype=application/octet-stream",
                    options: {
                        name: "[name].[ext]",
                        publicPath: "/assets/fonts",//以网站根路径（target目录）为准
                        outputPath: "assets/fonts"//target下的目录
                    }
                }]
            },

            {
                test: /\.svg(\?v=\d+\\d+\\d+)?$/,
                use: [{
                    loader: "url-loader?limit=10000&mimetype=image/svg+xml",
                    options: {
                        name: "[name].[ext]",
                        publicPath: "/assets/image",//以网站根路径（target目录）为准
                        outputPath: "assets/image"//target下的目录
                    }
                }]
            }
        ]
    },
    performance: {
        hints: false
    },
    //插件
    plugins: [
        new TransferWebpackPlugin([
            {
                from: 'assets',
                to: 'assets'
            }
        ], path.resolve(__dirname, "../src"))
    ]
};

//配置页面
let entryObj = util.getEntry();
let htmlArray = [];
Object.keys(entryObj).forEach(function (element) {
    htmlArray.push({
        _html: element.split(".md5.")[0],
        path: entryObj[element][0],
        title: '',
        chunks: [element, "manifest", "vendors", "common"]
    })
});
//自动生成html模板
htmlArray.forEach(function (element) {
    if (element._html !== "base") {
        module.exports.plugins.push(new HtmlWebpackPlugin(util.getHtmlConfig(element)));
    }
});