const JsonUtils=require("../src/public/script/JsonUtils.js");

const glob = require('glob');
const crypto = require('crypto');
const fs = require('fs');

//  build/utils.js external配置
const externalConfig = [
    {name: 'jquery', scope: 'jQuery', js: 'jquery.min.js'},
    // {name: 'bootstrap',cdnName:"twitter-bootstrap", scope: 'Bootstrap', js: 'js/bootstrap.min.js',css:'css/bootstrap.min.css'},
];

// build/utils.js 国内免费cdn镜像源
let cdnBaseHttp = 'https://cdn.bootcss.com';

//动态添加入口
let getEntry = () => {
    let entry = {};
    //加入公共js文件
    let base = [];
    base.push("./src/public/script/base.js");
    base.push('babel-polyfill');
    entry["base"] = base;
    //读取src目录所有page入口
    glob.sync('./src/entry/**/*.{js,ts}').forEach(function (path) {
        //读取一个Buffer
        let fsHash = crypto.createHash('md5');
        fsHash.update(fs.readFileSync(path));
        let start = path.indexOf('src/') + 10;
        let end = path.length - 3;
        let eArr = [];
        let name = path.slice(start, end);
        eArr.push(path);
        //path是js的路径
        //name是entry为根目录的entry文件(js、ts)的路径
        // entry[`${n}.md5.${fsHash.digest('hex')}`] = eArr;
        entry[`${name}.md5.${fsHash.digest('hex')}`] = eArr;
    });
    return entry;
};
//动态生成html
//获取html-webpack-plugin参数的方法
let getHtmlConfig = function (element) {
    let externalConfig = JSON.parse(JSON.stringify(this.externalConfig)); // 读取配置
    getExternalModules(externalConfig); // 获取到合适路径和忽略模块
    let chunks = element.chunks, path = element.path;
    let start = path.indexOf('src/') + 10;
    let end = path.length - 3;
    let name = path.slice(start, end);
    if (name !== "base") chunks.push('base');
    return {
        template: `./src/pages/${name}.html`,
        filename: `${name}.html`,
        inject: true,
        hash: false,
        chunks: chunks,
        cdnConfig: externalConfig, // cdn配置
        onlyCss: false, //加载css
        // 压缩html
        minify: {
            removeComments: true,
            collapseWhitespace: true,
            removeAttributeQuotes: true
        }
    }
};

let getModulesVersion = () => {
    let mvs = {};
    let regexp = /^npm_package_.{0,3}dependencies_/gi;
    for (let m in process.env) { // 从node内置参数中读取，也可直接import 项目文件进来
        if (regexp.test(m)) { // 匹配模块
            // 获取到模块版本号
            mvs[m.replace(regexp, '').replace(/_/g, '-')] = process.env[m].replace(/(~|\^)/g, '');
        }
    }
    return mvs;
};

let getExternalModules = config => {
    let externals = {}; // 结果
    let dependencieModules = getModulesVersion(); // 获取全部的模块和版本号
    config = config || externalConfig; // 默认使用utils下的配置
    config.forEach(item => { // 遍历配置
        if (item.name in dependencieModules) {
            let version = dependencieModules[item.name];
            // 拼接css 和 js 完整链接
            item.css = item.css && [item.cdnBaseHttp==null?cdnBaseHttp:item.cdnBaseHttp, item.cdnName||item.name, version, item.css].join('/');
            item.js = item.js && [item.cdnBaseHttp==null?cdnBaseHttp:item.cdnBaseHttp, item.cdnName||item.name, version, item.js].join('/');
            externals[item.name] = item.scope; // 为打包时准备
        } else {
            throw new Error('相关依赖未安装，请先执行npm install ' + item.name);
        }
    });
    return externals;
};

let getModules = () => {
    let externals = {}; // 结果
    let dependencies =  JsonUtils.objToStrMap( JSON.parse(fs.readFileSync('./package.json', 'utf8'))["dependencies"]);
    dependencies.delete("@babel/runtime");
    dependencies.forEach((value,key)=>{
        externals[`${key}`]=key;
    });
    return externals;
};

module.exports = {getEntry, getHtmlConfig, externalConfig ,getExternalModules,getModules};

