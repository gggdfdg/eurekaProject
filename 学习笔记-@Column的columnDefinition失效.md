# 学习笔记-@Column的columnDefinition失效
 
------

首先介绍下本章学习的内容：
 
> * 学习笔记-@Column的columnDefinition失效

## 学习笔记-@Column的columnDefinition失效
解决方法---#spring.jpa.properties.hibernate.globally_quoted_identifiers=false
```seq
  #spring.jpa.properties.hibernate.globally_quoted_identifiers=true
  开启后, 创建sql语句执行时会添加’`’, 会造成columnDefinition 属性失效
  例如:
  alter table `xxx` add column `xxx` `varchar(50) default ''`
  关闭后
  alter table xxx add column xx varchar(50) default ''
  可以看出: 有舍有得, 第二种要求字段/表等命名不能和mysql或其他数据库中关键字重名
```

