package com.ming.eureka.security;

import com.ming.eureka.dto.Constant;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截器Interceptor：对过往的连接/对象进行判断，
 * 如果符合条件那么放行/做其他处理，如果不符合条件那么提示并false拦截
 */
@Component  //拦截器也是一个组件，需要加@Component注解进行组件注册
public class AuthInterceptor implements HandlerInterceptor {
    //声明一个static final的Logger对象
    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    public org.springframework.security.core.userdetails.User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof String){
            return null;
        }else{
            return (org.springframework.security.core.userdetails.User)principal;
        }
    }

    /**
     * 预处理回调方法，实现处理器的预处理
     * 返回值：true表示继续流程；false表示流程中断，不会继续调用其他的拦截器或处理器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("开始拦截.........");
        //设置response的编码格式
        response.setContentType("text/html;charset=utf-8");
        //获取请求的url
        String url = request.getServletPath().toString();
        System.out.println("url:" + url);

        org.springframework.security.core.userdetails.User currentUser = getCurrentUser();
        if(ObjectUtils.isEmpty(currentUser)){
            //没登录的话，让spring security去拦截
            return true;
        }
        //这几个接口一直可以访问
        if(url.startsWith("/webjars/")){
            return true;
        }
        if(url.contains("/api/logout")){
            return true;
        }
        if(url.contains("/swagger-resource")){
            return true;
        }
        if(url.contains("/v2/api-docs")){
            return true;
        }
        if(url.contains("/swagger-ui.html")){
            return true;
        }
        if(url.equals("/api") || url.equals("/api/")){
            return true;
        }
        if(url.equals("/api/login")){
            return true;
        }
        if(url.equals("/api/token")){
            return true;
        }
        if(url.equals("/api/server")){
            return true;
        }
        if(url.equals("/api/script")){
            return true;
        }
        //获取request中的参数token
        String token = request.getHeader(Constant.TOKEN_HEADER_STRING);
        //如果token为空或不存在
        if (token == null || "".equals(token) || !token.startsWith(Constant.TOKEN_PERFIX)) {
            logger.info("{} : Unknown token", request.getServletPath());
            //将结果打印返回到前端
            response.getWriter().print("The resource requires authentication, which was not supplied with the request");
            return false;//拦截成功
        }
        //解析token
        Claims claims = TokenUtils.parseToken(token);
        String userName = (String) claims.get("username");
        Date expireTime = claims.getExpiration();
        //如果token的username不存在
        if (!currentUser.getUsername().equals(userName)) {
            logger.info("{} : token user not found", request.getServletPath());
            response.getWriter().print("ERROR Permission denied");
            return false;
        }
        //如果token过期
        if (expireTime.before(new Date())) {
            logger.info("{} : token expired", request.getServletPath());
            response.getWriter().print("The token expired, please apply for a new one");
            return false;
        }
        //token匹配成功，放行
        request.setAttribute(Constant.CURRENT_USER, userName);
        System.out.println("放行...........");
        return true;
    }

    /**
     * 后处理回调方法，实现处理器（controller）的后处理，但在渲染视图之前
     * 此时我们可以通过modelAndView对模型数据进行处理或对视图进行处理
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 整个请求处理完毕回调方法，即在视图渲染完毕时回调，
     * 如性能监控中我们可以在此记录结束时间并输出消耗时间，
     * 还可以进行一些资源清理，类似于try-catch-finally中的finally，
     * 但仅调用处理器执行链中
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
