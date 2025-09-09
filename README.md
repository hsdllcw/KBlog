# KBlog
<p align="center"><a href="https://blog.csdn.net/hsdllcw"><img width="120" src="https://avatar.csdnimg.cn/A/C/5/3_hsdllcw.jpg"></a></p>

<p align="center">基于 Kotlin 和 OpooPress 构建的博客系统</p>

<p align="center">
  <a href="https://github.com/vuejs/vue" rel="nofollow" target="_blank">
    <img src="https://img.shields.io/badge/vue-2.6.10-brightgreen.svg" alt="vue">
  </a>
  <a href="https://github.com/ElemeFE/element" rel="nofollow" target="_blank">
    <img src="https://img.shields.io/badge/element--ui-2.9.2-brightgreen.svg" alt="element-ui">
  </a>
</p>

## 简介
KBlog 是一个创新的博客平台，将动态管理的便利性与静态部署的高性能完美结合。通过友好的 Web 界面管理内容，一键生成纯静态站点，可零成本部署到 GitHub Pages、OSS 等平台。基于 Java 技术栈，支持桌面模式随用随开和服务器模式团队协作，让每个人都能轻松拥有个性化博客。
          
          本项目作为本人的毕业设计而存在，与2020年论文答辩后，5月份左右开源。

## 写在前面
```
①如果在Java Web服务器中部署war包，需要MySQL中新建名为kblog的数据库，数据库编码为utf8mb4，用户名为root，数据库密码为123456。
这里可以修改默认数据库的用户名和密码(用EncryptionTest这个测试类中加密数据，然后到kblog/src/main/resources/application.yml文件里写数据库的用户名和密码)
如果通过java -jar命令启动war包，直接启动就好，其他什么都不用做:)。
②项目运行的环境为Java 17
③使用Node为v12.16.2以上版本运行(以下的版本未测试)
④项目有两种启动方式：如果部署进tomcat服务器，编译好的静态博客网站会直接更新在tomcat的部署目录下；如果使用java -jar命令启动，编译好的静态博客网站放在执行命令的目录下（可以通过查看启动日志确定绝对路径）的target目录中
```
## 快速开始
``` bash
java -jar kblog-1.0.0-SNAPSHOT.war
```
浏览器访问 [http://localhost:8080](http://localhost:8080)
## Build Setup

``` bash
# 克隆项目
git clone https://github.com/hsdllcw/KBlog.git

# 进入项目
cd KBlog

# install dependencies
mvn package

# 前端部分
cd web
# 建议不要用 cnpm 安装 会有各种诡异的bug 可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# serve with hot reload at localhost:3000
npm run dev
```

浏览器访问 [http://localhost:3000](http://localhost:3000)

## 功能
```
- 登录/注销
- 文章编辑
- 静态网站生成
- 栏目标签管理
```
## Browsers support

Modern browsers and Internet Explorer 10+.

| [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png" alt="IE / Edge" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>IE / Edge | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png" alt="Firefox" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Firefox | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png" alt="Chrome" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Chrome | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png" alt="Safari" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Safari |
| --------- | --------- | --------- | --------- |
| IE10, IE11, Edge| last 2 versions| last 2 versions| last 2 versions

## License

[MIT](https://github.com/hsdllcw/KBlog/blob/master/LICENSE)

Copyright (c) 2020-hsdllcw
