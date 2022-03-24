/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.controller;

import com.ming.eureka.Constant;
import com.ming.eureka.ValidateCodeUtils;
import com.ming.eureka.model.entity.sysuser.SysUser;
import com.ming.eureka.model.entity.user.CurrentUser;
import com.ming.eureka.query.ParameterChecker;
import com.ming.eureka.query.QueryInfo;
import com.ming.eureka.query.QueryUtils;
import com.ming.eureka.security.CurrentSysUser;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.Page;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * web的基础controller
 *
 * @author lll 2015年5月28日
 */
@SuppressWarnings("Duplicates")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BaseController {

    /**
     * 编码器
     */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 枚举查询类型，区分系统支持的查询操作类型。
     */
    protected enum QueryType {
        //来源查询，存在session中，下次可以直接拿出来查询
        FORM_QUERY,
        //分页查询，存在session中，下次可以直接拿出来查询
        PAGE_QUERY,
        //排序查询，存在session中，下次可以直接拿出来查询
        ORDER_QUERY,
        //后退查询，存在session中，下次可以直接拿出来查询
        BACK_QUERY
    }

    //基础请求回复部分------------------

    /**
     * 获取项目的基础uri，例如，访问http://web:8004/
     *
     * @return {@link String}
     */
    public static String getBaseUrl() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            return StringUtils.substringBeforeLast(request.getRequestURL().toString(), request.getServletPath());
        } else {
            throw new ServiceException("错误调用，错误上下文");
        }
    }

    /**
     * Request
     *
     * @return {@link HttpServletRequest}
     */
    private HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    /**
     * Response
     *
     * @return {@link HttpServletResponse}
     */
    private HttpServletResponse getResponse() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getResponse();
    }


    //验证码部分--------------------

    /**
     * 绕过Template,直接输出验证码(image/jpeg)
     *
     * @param sessionKey 会话密钥
     */
    protected void renderValidateImg(String sessionKey) {
        try {
            HttpServletRequest request = this.getRequest();
            HttpServletResponse response = this.getResponse();

            String code = ValidateCodeUtils.getInstance().getRandomString();
            request.getSession().setAttribute(sessionKey, code);

            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            OutputStream outputStream = response.getOutputStream();
            ValidateCodeUtils.getInstance().string2img(code, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }


    /**
     * 获取会话用户
     *
     * @return {@link CurrentSysUser}
     */
    public CurrentSysUser getCurrentSysUser() {
        return (CurrentSysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    //密码部分--------------------------

    /**
     * 验证密码
     *
     * @param user        用户
     * @param oldPassword 旧密码
     * @return boolean
     */
    public boolean validPassword(SysUser user, String oldPassword) {
        return encoder.matches(oldPassword, user.getPassword());
    }

    /**
     * 加密密码
     *
     * @param passWord 密码
     * @return {@link String}
     */
    protected String enTryPtPassword(String passWord) {
        return encoder.encode(passWord);
    }



    //分页部分------------------------------

    /**
     * 处理分页前后省略号计算问题
     *
     * @param model 模型
     * @param page  页面
     */
    public void pageParamResolve(Model model, Page<?> page) {

        Assert.notNull(model, "model不许与为空");
        Assert.notNull(page, "page不许与为空");

        boolean omittedFirstFlag = false; // 首页是否省略

        boolean omittedEndFlag = false; // 尾页是否省略

        int minIndex;

        int maxIndex;

        int maxPage = page.getTotalPages();

        int currentPage = page.getNumber() + 1;

        int PAGE_DISPLAY_MAX_NUM = 10;
        int PAGE_DISPLAY_MAX_BETWEEN = 4;
        if (maxPage > PAGE_DISPLAY_MAX_NUM + 1 && currentPage > (PAGE_DISPLAY_MAX_BETWEEN + 1)) {
            omittedFirstFlag = true;
        }

        if (maxPage - currentPage > PAGE_DISPLAY_MAX_BETWEEN) {
            if (currentPage > PAGE_DISPLAY_MAX_BETWEEN) {
                if (currentPage != (PAGE_DISPLAY_MAX_BETWEEN + 1)) {
                    maxIndex = currentPage + PAGE_DISPLAY_MAX_BETWEEN;
                } else {
                    maxIndex = currentPage + PAGE_DISPLAY_MAX_BETWEEN + 1;
                }
            } else if (maxPage > PAGE_DISPLAY_MAX_NUM) {
                maxIndex = PAGE_DISPLAY_MAX_NUM;
            } else {
                maxIndex = maxPage;
            }
            if (maxPage > PAGE_DISPLAY_MAX_NUM) {
                omittedEndFlag = true;
            }
        } else {
            maxIndex = maxPage;
        }

        if (maxIndex == maxPage && omittedFirstFlag) {
            if (maxIndex - currentPage <= PAGE_DISPLAY_MAX_BETWEEN) {
                minIndex = maxIndex - PAGE_DISPLAY_MAX_NUM + 1;
            } else {
                minIndex = currentPage + PAGE_DISPLAY_MAX_NUM - maxIndex;
            }
        } else if (omittedFirstFlag) {
            minIndex = currentPage - PAGE_DISPLAY_MAX_BETWEEN;
        } else {
            minIndex = 1;
        }

        model.addAttribute(omittedFirstFlag)
                .addAttribute(omittedEndFlag)
                .addAttribute(maxPage)
                .addAttribute(minIndex)
                .addAttribute(maxIndex);
    }


    //基础查询部分----------------------------

    /**
     * 保存查询信息到session中存储
     *
     * @param defaultOrder   默认的顺序
     * @param defaultOrderBy 默认的命令
     */
    protected QueryInfo saveQueryInfoAndReturnPage(String defaultOrder, String defaultOrderBy) {
        return this.saveQueryInfoAndReturnPage();
    }

    /**
     * 保存查询信息和返回页面
     */
    protected QueryInfo saveQueryInfoAndReturnPage() {
        this.saveRequestParameter();

        int pageNumber = 0;
        int isBack = 0;
        String orderBy = null;
        String order = null;

        if (!ParameterChecker.isNullOrEmpty(this.getRequest().getParameter("isBack"))) {
            isBack = Integer.valueOf(this.getRequest().getParameter("isBack"));
        }
        if (!ParameterChecker.isNullOrEmpty(this.getRequest().getParameter("pageNumber"))) {
            pageNumber = Integer.valueOf(this.getRequest().getParameter("pageNumber"));
        }
        if (!ParameterChecker.isNullOrEmpty(this.getRequest().getParameter("order"))) {
            order = this.getRequest().getParameter("order");
        }
        if (!ParameterChecker.isNullOrEmpty(this.getRequest().getParameter("orderBy"))) {
            orderBy = this.getRequest().getParameter("orderBy");
        }
        return this.saveQueryInfo(pageNumber, orderBy, order);
    }

    /**
     * 保存请求参数
     */
    private void saveRequestParameter() {
        String className = this.getClass().getName();

        HttpServletRequest request = this.getRequest();
        String redirectURI = request.getRequestURI();

        request.getSession().setAttribute(className + "[listPath]", redirectURI);
    }


    /**
     * 保存查询信息
     *
     * @param pageNumber 页码
     * @param orderBy    命令
     * @param order      订单
     * @return {@link QueryInfo}
     */
    private QueryInfo saveQueryInfo(int pageNumber, String orderBy, String order) {
        QueryInfo queryInfo = QueryUtils.getQueryInfoFromSession(this.getRequest(), this.getClass().getName());

        switch (this.getQueryType()) {
            case FORM_QUERY:
                queryInfo.setPageNumber(1);
                Map<String, String> requestParamMap = createParameterMapFromRequest();
                queryInfo.setRequestParamMap(requestParamMap);
                break;
            case PAGE_QUERY:
                queryInfo.setPageNumber(pageNumber);
                break;
            case ORDER_QUERY:
                queryInfo.setOrderBy(orderBy);
                queryInfo.setOrder(order);
                break;
            case BACK_QUERY:
                break;
            default:
                throw new RuntimeException("非法的查询类型");
        }

        this.setSearchParamsBackRequest(queryInfo);
        QueryUtils.updateQuerySession(this.getRequest());
        return queryInfo;
    }

    /**
     * 根据分页相关变量，获取当前查询类型
     *
     * @return {@link QueryType}
     */
    private QueryType getQueryType() {
        HttpServletRequest request = this.getRequest();
        if (!ParameterChecker.isNullOrEmpty(request.getParameter("pageNumber"))) {
            return QueryType.PAGE_QUERY;
        }
        if (!ParameterChecker.isNullOrEmpty(request.getParameter("order"))
                && !ParameterChecker.isNullOrEmpty(request.getParameter("orderBy"))) {
            return QueryType.ORDER_QUERY;
        }
        if (!ParameterChecker.isNullOrEmpty(request.getParameter("isBack"))) {
            return QueryType.BACK_QUERY;
        }
        return QueryType.FORM_QUERY;
    }


    /**
     * 将request对象中的参数map转化成标准的map对象,checkbox多个提交参数的情况，使用,分割values
     *
     * @return {@link Map<String, String>}
     */
    private Map<String, String> createParameterMapFromRequest() {
        Map<String, String> map = new HashMap<>();
        HttpServletRequest request = this.getRequest();
        Set<Map.Entry<String, String[]>> set = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> p : set) {
            String key = p.getKey();
            String[] values = p.getValue();
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0, len = values.length; i < len; i++) {
                valueStr.append(values[i]);
                if (len != (i + 1)) {
                    valueStr.append(",");
                }
            }
            map.put(key, valueStr.toString());
        }
        return map;
    }

    /**
     * 将请求参数重新配置会request对象，提供页面使用jsTl进行获取的能力
     *
     * @param queryInfo 查询信息
     */
    private void setSearchParamsBackRequest(QueryInfo queryInfo) {
        HttpServletRequest request = this.getRequest();
        Map<String, String> tm = queryInfo.getRequestParamMap();
        if (tm != null) {
            Set<Map.Entry<String, String>> set = tm.entrySet();
            for (Map.Entry<String, String> e : set) {
                request.setAttribute(e.getKey(), e.getValue());
            }
        }
    }

    /**
     * 返回原页面跳转
     *
     * @throws Exception 异常
     */
    @RequestMapping("/backList")
    @ResponseBody
    public void backList() throws Exception {
        String className = this.getClass().getName();

        HttpServletRequest request = this.getRequest();
        HttpSession session = request.getSession();
        HttpServletResponse response = this.getResponse();

        String redirectListPath = (String) session.getAttribute(className + "[listPath]");
        // 如果之前没有显式保存过返回地址，就默认返回toList方法。
        if (redirectListPath == null) {
            redirectListPath = request.getRequestURI().replace("/backList", "/list");
        }
        response.sendRedirect(redirectListPath + "?isBack=1");
    }

}
