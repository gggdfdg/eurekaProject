# 学习笔记-urls静态资源缓存刷新
 
------

首先介绍下本章学习的内容：
 
> * 学习笔记-urls静态资源缓存刷新

## 学习笔记-urls静态资源缓存刷新
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