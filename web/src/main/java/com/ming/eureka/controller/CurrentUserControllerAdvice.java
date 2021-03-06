package com.ming.eureka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * controller增强器，全局参数处理
 * @author lazier
 */
@ControllerAdvice
public class CurrentUserControllerAdvice {

    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    @Value("${sys.config.name}")
    private String sysName;

    @Value("${sys.config.logo.url}")
    private String sysImgUrl;

    /**
     * 全局参数sysName
     * @return
     */
    @ModelAttribute("sysName")
    public String getSysName() {
        return this.sysName;
    }

    /**
     * 全局参数sysImgUrl
     * @return
     */
    @ModelAttribute("sysImgUrl")
    public String getSysImgUrl() {
        return this.sysImgUrl;
    }


    //方法前景
    //原来spring boot会把静态文件缓存到浏览器本地。但这样就造成了一个问题：如果服务器静态文件修改，浏览器端在未过期之前是不会重新加载文件的。
    //此时需要通过版本号来控制。spring boot版本号支持两种，一种是文件md5，另一种是固定版本号。
    //我采用的是md5方式，spring boot启动时会计算每个静态文件的md5值并缓存，浏览器访问时每个静态文件后缀前加上md5值作为版本号，如果服务器md5值改变则浏览器重新加载。
    //（需要重启应用才会重新生成md5）
    /**
     *  静态资源处理
     *  使用ResourceUrlProvider进行版本管理
     *  并避免在版本发生改变时，由于浏览器缓存而产生资源版本未改变的错误
     */
    @ModelAttribute("urls")
    public ResourceUrlProvider urls() {
        return this.resourceUrlProvider;
    }

    //需要在自己的ftl加上这个方法
    //<#-- 静态资源版本化url处理 -->
    //<#function static url>
    //	<#return urls.getForLookupPath(ctx+url)>
    //</#function>


}