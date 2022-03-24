/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.restcontroller;

import javax.servlet.http.HttpServletRequest;

import com.ming.eureka.model.dao.user.UserDao;
import com.ming.eureka.model.entity.user.User;
import com.ming.eureka.util.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * RestController 基类
 * @author lll 2015年7月13日
 */
@Data
@Slf4j
public class RestBaseController {
	@Autowired
	private UserDao userDao;
	
	/**
	 * 获取当前登录设备
	 * @return
	 */
	public User getCurrentUser() {
		String username = this.getCurrentUsername();
		if(username == null){
			return null;
		}

		return userDao.findByLoginName(username);
	}

	/**
	 * 获取当前登录用户名
	 * @return
	 */
	public String getCurrentUsername() {
		if (!SecureUtil.isAuthenticated() || !SecureUtil.isApi()) {
			return null;
		}
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails)principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}
	
	/**
	 * 获取链接
	 */
	public static String getHost() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    if(null != attr && attr instanceof ServletRequestAttributes) {
	    	  HttpServletRequest request = ((ServletRequestAttributes)attr).getRequest();
	    	  return StringUtils.substringBeforeLast(request.getRequestURL().toString(), request.getServletPath());
    	}
    	else {
    		throw new ServiceException("错误调用，错误上下文");
    	}
	}
	
}
