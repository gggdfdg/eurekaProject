# spring cloud和boot的兼容性
 
------

首先介绍下本章学习的内容：
 
> * 兼容性
> * javax.servlet-api升级

## 兼容性
去https://spring.io/projects/spring-cloud官网看看兼容性，发现兼容性写这样

| Release Train        | Boot Version   |
| --------   | -----:  |
|2021.0.x aka Jubilee| 2.6.x| 
|2020.0.x aka Ilford|2.4.x, 2.5.x (Starting with 2020.0.3)|
|Hoxton|2.2.x, 2.3.x (Starting with SR5)|
|Greenwich|2.1.x|
|Finchley|2.0.x|
|Edgware|1.5.x|
|Dalston|1.5.x|

#### 1. 2021.0.x aka和2.6.x不兼容
 
 点击2021.0.x aka Jubilee后显示如下：
 
```seq
   Spring Cloud 2021.0 Release Notes
   Spencer Gibb edited this page on 2 Dec 2021 · 4 revisions
    Pages 7
   Find a Page…
   Home
   Release Train Naming Convention
   Spring Cloud 2020.0 Release Notes
   Spring Cloud 2021.0 Release Notes
   2021.0.0
   2021.0.0-RC1
   2021.0.0-M3
   2021.0.0-M2
   Spring Cloud 2022.0 Release Notes
   Spring Cloud Hoxton Release Notes
   Supported Versions
   Clone this wiki locally
   https://github.com/spring-cloud/spring-cloud-release.wiki.git
   2021.0.0
   See the project page for all the issues and pull requests included in this release.
   
   2021-12-01
   
   Spring Cloud Openfeign 3.1.0 (issues)
   Spring Cloud Cloudfoundry 3.1.0
   Spring Cloud Config 3.1.0 (issues)
   Spring Cloud Commons 3.1.0 (issues)
   Spring Cloud Contract 3.1.0 (issues)
   Spring Cloud Consul 3.1.0
   Spring Cloud Gateway 3.1.0 (issues)
   Spring Cloud Netflix 3.1.0 (issues)
   Spring Cloud Starter Build 2021.0.0
   Spring Cloud Zookeeper 3.1.0
   Spring Cloud Circuitbreaker 2.1.0 (issues)
   Spring Cloud Vault 3.1.0
   Spring Cloud Task 2.4.0 (issues)
   Spring Cloud Kubernetes 2.1.0 (issues)
   Spring Cloud Sleuth 3.1.0 (issues)
   Spring Cloud Bus 3.1.0
   Spring Cloud Cli 3.1.0
```
我们选择spring cloud 3.1，然后spring boot 我们选择2.6.1，发现会报莫名其妙的错误。。。。


#### 2020.0.x aka Ilford和2.4.x, 2.5.x (Starting with 2020.0.3)可以兼容

我们选择spring cloud 3.0.5和spring boot 2.5.8发现是可以兼容

## javax.servlet-api升级

然后当我们的服务器供着新增依赖如下

```seq
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        <version>3.0.5</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.5.8</version>
    </dependency>
```
启动发现会报错java.lang.NoSuchMethodError: 'java.lang.String javax.servlet.ServletContext.
这是因为javax.servlet版本过低，去dependencies发现依赖javax.servlet只有2.x，于是我们暴力升级加个依赖

```seq
     <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>javax.servlet-api</artifactId>
                <version>3.1.0</version>
                <scope>provided</scope>
     </dependency>
```

终于可以了