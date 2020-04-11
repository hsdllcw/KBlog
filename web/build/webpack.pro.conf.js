const webpack = require('webpack');
const webpackConfig = require('./webpack.base.conf');
const merge = require('webpack-merge');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = merge(webpackConfig,{
    mode: "production",
    devtool: false,
    plugins:[
        new CleanWebpackPlugin(),
        // 提取至单独css文件
        new MiniCssExtractPlugin({
            filename: 'bundle/[name].[contenthash:8].css',
            chunkFilename: 'bundle/[name].[contenthash:8].css'
        }),
        // 当chunk没有名字时，保持chunk.id稳定（缓存chunk）
        new webpack.NamedChunksPlugin(chunk => {
            if (chunk.name) {
                return chunk.name
            }
            const modules = Array.from(chunk.modulesIterable);
            if (modules.length > 1) {
                const hash = require('hash-sum');
                const joinedHash = hash(modules.map(m => m.id).join('_'));
                let len = 4;
                const seen = new Set();
                while (seen.has(joinedHash.substr(0, len))) len++;
                seen.add(joinedHash.substr(0, len));
                return `chunk-${joinedHash.substr(0, len)}`;
            } else {
                return modules[0].id;
            }
        }),

        // 当vender模块没有变化时，保持module.id稳定（缓存vender）
        new webpack.HashedModuleIdsPlugin()
    ],
    // 提取公共模块，包括第三方库和自定义工具库等
    optimization: {
        // 找到chunk中共享的模块,取出来生成单独的chunk
        splitChunks: {
            chunks: "all",  // async表示抽取异步模块，all表示对所有模块生效，initial表示对同步模块生效
            minChunks: 1,  //被多少模块共享
            name: true,  //打包后的名称，默认是chunk的名字通过分隔符（默认是～）分隔开，如vendor~
            cacheGroups: {
                vendors: {  // 抽离第三方插件
                    name: 'vendors', // 打包后生成的js文件名称
                    test: /[\\/]node_modules[\\/]/,
                    priority: 10,
                    minSize: 0,     // 将引用模块分离成新代码文件的最小体积
                    minChunks: 1,
                    chunks: 'initial' // 只打包初始时依赖的第三方
                },
                utilCommon: {   // 抽离自定义工具库
                    name: "common",
                    minSize: 0,     // 将引用模块分离成新代码文件的最小体积
                    minChunks: 2,   // 表示将引用模块如不同文件引用了多少次，才能分离生成新chunk
                    priority: -20
                }
            }
        },
        // 为 webpack 运行时代码创建单独的chunk
        runtimeChunk: {
            name: 'manifest'
        },
        minimizer: [ // 压缩js、压缩css配置
            new TerserPlugin({
                sourceMap: false,
                cache: true,
                parallel: true
            }),
            new OptimizeCSSAssetsPlugin()
        ],
        minimize: true
    },
});