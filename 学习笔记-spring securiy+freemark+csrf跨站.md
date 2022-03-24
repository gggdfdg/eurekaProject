# 跨域和spring security和freemark如何并用
 
------

首先介绍下本章学习的内容：
 
> * spring security开启csrf防止跨站
> * 开启csrf后，/logout变成post提交的处理
> * 开启csrf后，/login变成post提交的处理
> * freemark前端新增参数，合理调用post

## spring security开启csrf防止跨站
默认以后的所有版本spring security是开启csrf

## 开启csrf后，/logout变成post提交的处理
当开启csrf后，/logout也就是退出变成post请求了，这时候需要提交_csrf参数才能退出
或者调用logoutRequestMatcher方法，显示设置/logout请求为GET方法

## 开启csrf后，/login变成post提交的处理
当开启csrf后，/login，这时候需要提交_csrf参数才能登录
我们有两种处理方案
1：添加_csrf参数
```seq
<div class="login_r">
    <div class="ibox-content login_box">
        <div class="login_title">
            登录<em>sign in</em>
        </div>
        <form id="loginForm" class="m-t" method="post" onsubmit="return checkField()"
              enctype="application/x-www-form-urlencoded" action="/login" >
            <input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
            <div class="form-group">
                <input type="text" id="username" name="username" class="form-control" placeholder="用户名">
            </div>
            <div class="form-group">
                <input type="password" id="password" name="password" class="form-control" placeholder="密码">
            </div>
            <div class="form-group safe_code_div">
                <input type="text" maxlength="4" id="safecode" name="safecode"
                       class="form-control auth_code" placeholder="验证码">
                <a class="m_l_10 codeimg" onclick="return false;" href="javascript:;">
                    <img id="safeCode" src="/loginp/getSafeCode" height="34" width="100">
                </a>
            </div>
            <button type="submit" class="btn btn-primary block full-width m-b">登&nbsp;录</button>
            <p class="text-danger" style="display: none;"></p>
        </form>
    </div>
</div>
```
2：跨站防护跳过csrf
```
    http.csrf().ignoringAntMatchers("/login","/logout");
```

## freemark前端新增参数，合理调用post
当开启csrf后，所有的post请求都必须加_csrf
```
<input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
```

