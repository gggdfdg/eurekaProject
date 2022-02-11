按
> * 学习笔记-集群
> * 学习笔记-git
> * 学习笔记-spring cloud兼容boot
> * 学习笔记-aes对称加密
> * 学习笔记-@Column的columnDefinition失效
> * 学习笔记-(spring-boot版本管理)

可以学会所有课程

## web系统的登录使用
在b_sysconfig数据表配置上加一条服务器配置
然后再b_sysuser加个用户，密码用
PasswordEncoder encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("abc1233"));
加密
