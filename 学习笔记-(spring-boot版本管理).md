# spring-boot版本管理
 
------

首先介绍下本章学习的内容：
 
> * spring-boot版本管理（防止出现spring boot多插件包版本不一致报错）

## spring-boot版本管理
可以在父类添加如下代码
```seq
  <!--版本管理，防止子模块spring-boot引用不一致的spring boot包-->
  <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <!--spring-boot子插件默认版本-->
          <version>2.5.8</version>
          <relativePath/>
  </parent>
```

