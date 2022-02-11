package com.ming.eureka.security;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.Lists;
import com.ming.eureka.CommonResult;
import com.ming.eureka.IpUtils;
import com.ming.eureka.model.entity.MailMessage;
import com.ming.eureka.model.service.sysuser.MailService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.beans.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * 异常拦截
 */
public class ExceptionFilter implements Filter {

    private JsonMapper jsonMapper = new JsonMapper();
    private MailService mailService;

    private String[] environment;

    private String emailReceiver;
    private String excptionEnvironment;
    private static Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {   //如果有异常则捕捉
            List<Throwable> exceptions = ExceptionUtils.getThrowableList(e);
            StringBuffer buffer = new StringBuffer();
            for (Throwable throwable : exceptions) {
                if (exceptions.get(0) != throwable) {
                    logger.error(throwable.getMessage(), throwable);
                    buffer.append(Arrays.asList(throwable.getStackTrace()).toString());
                    buffer.append(",-----------**************-----------,");
                }
            }
            List<String> excptionEnvironmentList = Arrays.asList(excptionEnvironment.split(","));
            if (excptionEnvironmentList.stream().anyMatch(n -> ArrayUtils.contains(environment, n))) {
                if (!StringUtils.isBlank(emailReceiver)) {
                    List<String> emailList = Arrays.asList(emailReceiver.split(","));
                    Enumeration enu = ((HttpServletRequest) request).getParameterNames();
                    StringBuffer route = ((HttpServletRequest) request).getRequestURL();
                    boolean flag = true;
                    if (!StringUtils.isBlank(((HttpServletRequest) request).getQueryString())) {
                        route.append("?");
                        route.append(((HttpServletRequest) request).getQueryString());
                    }
                    while (enu.hasMoreElements()) {
                        if (flag) {
                            route.append("<br/>请求参数：");
                            flag = false;
                        } else {
                            route.append("&");
                        }
                        String paraName = (String) enu.nextElement();
                        route.append(paraName + "=" + request.getParameter(paraName));
                    }
                    String clientIp = "请求的IP：" + IpUtils.getIpAddr(((HttpServletRequest) request));
                    MailMessage message = new MailMessage(Lists.newArrayList(emailList), e.getCause().toString(), "请求路径：" + route.toString() + "<br/>" + clientIp + "<br/>异常信息：" + buffer.toString().replace(",", "<br/>"), null);
                    mailService.send(message);
                }
            }
            if (WebUtil.isAjax((HttpServletRequest) request)) {
                response.setContentType("text/json");
                response.setCharacterEncoding("UTF-8");
                CommonResult result = CommonResult.commError(e.getMessage());
                PrintWriter out = response.getWriter();
                out.print(jsonMapper.writeValueAsString(result));
                out.flush();
                out.close();
            } else {
                try {
                    request.setAttribute("error", e.getMessage());//存储业务异常信息类
                    request.getRequestDispatcher("/500").forward(request, response);//跳转到信息提示页面！！
                    return;
                } catch (Exception ex) {

                }
            }
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
        ServletContext context = arg0.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        environment
                = ((StandardServletEnvironment) ctx.getBean("environment")).getActiveProfiles();
        mailService = (MailService) ctx.getBean("mailService");
        Properties props = new Properties();
        try {
            InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("application-common.properties");
            props.load(in);
            emailReceiver = props.getProperty("email.receiver");
            excptionEnvironment = props.getProperty("excption.environment");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}