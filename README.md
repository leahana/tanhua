# 探花交友demo

#### 介绍

普通聊天交友demo

#### 软件架构

spring-boot.version: 2.3.9.RELEASE

spring-cloud.version: Hoxton.SR10

spring-cloud-alibaba.version: 2.2.5.RELEASE



微服务中间件:Gateway + RabbitMQ + Dubbo + nacos 

数据库：MySQL(mybatis-plus) + Redis + MongoDB (位置功能使用2D平面索引)

文件存储: + FastDFS(视频,音频文件) + OSS(图片)

第三方服务: 阿里云短信,Oss,内容审核 百度人脸识别,

#### 安装教程

1. 没有私有仓库依赖，可以直接使用maven构建

#### 使用说明

1. 前端管理员页面 在nginx镜像中,手机端 apk 数据库数据在static文件夹中
2. 部分测试可以使用ApiFox的接口测试工具，查看接口返回结果
3. 数据库数据较少,测灵魂功能推荐数据是假数据 
