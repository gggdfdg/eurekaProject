package com.ming.eureka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;

    /**
     * 添加拦截器
     * addPathPatterns 用于添加拦截规则，/**表示拦截所有请求
     * excludePathPatterns 排除拦截
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册AuthInterceptor拦截器
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

}
