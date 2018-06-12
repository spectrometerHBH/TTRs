# Web TTRS开发文档

## 简介

软件名称：NAIVE订票系统

开发环境：windows10、ubuntu16.04、Debian 9

测试环境：windows10、ubuntu16.04、Debian 9

开发技术：HTML+CSS+Javascript+AJAX+Python2.7

使用框架：Flask 1.0.2（Werkzeug0.14.1 Jinja2.10) 、Bootstrap4、Jquery

开发人员：苏起冬

## 文件功能说明

```
/static/               #储存静态文件
/template/             #储存模板文件
/app.py                #网站主要功能
/client.py             #与后端通信
/jsontool.py           #在结构化数据和普通字符串之间转换，用于前后端通信、与移动端通信
/__init__.py
```

## 模块功能说明

### URL

​	这是一个网站最基本的功能。在这个网站里面，在/action/下的URL负责处理各种请求，而根目录下的URL是直接面向用户的界面。

- / 首页
- /user/\<userid\> 查询id为userid的用户信息
- /login 旧的登陆界面，已经被废弃
- /query 查询车票界面
- /query_train 查询车次界面
- /query_user 查询用户信息界面
- /query_order 查询购票记录
- /add_train 增加车次
- /modify_train 修改车次
- /signup 惨遭废弃的注册界面
- /manage_train 管理车次界面
- /action/login 处理登陆
- /action/signup 处理注册
- /action/modify_profile 处理修改用户信息
- /action/query_order 处理查询购票记录请求
- /action/logout 处理登出请求
- /action/query_user 处理查询用户信息请求
- /action/query 处理查询车票请求
- /action/buy 处理购票请求
- /action/refund 处理退票请求
- /action/add_train 处理加车请求
- /action/modify_train 处理修改车次请求
- /action/del_train 处理删除车次的请求
- /action/sale_train 处理发售车次的请求
- /action/list_train 处理列出所有未出售车次的请求（用于管理车次界面）
- **/action/post与移动端通信**

### Session

​	session主要用于保存用户登录信息

### 与移动端通信

​	在/action/post下，接受从移动端发来的经过AES加密后的json信息（POST方式），并返回经过AES加密后的json信息。

### 前端的前端

​	主要使用bootstrap，并使用自己写的CSS做了一些小修改。表单验证方面使用了jquery validation，车站名的自动完成使用了jquery autocomplete， 增加车次里面的表格使用了js-grid。AJAX使用的是jquery提供的接口。

# 开发心得

累死老子了。