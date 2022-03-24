/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.dao.user;

import com.ming.eureka.model.BaseDao;
import com.ming.eureka.model.entity.user.IUser;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 
 * @author lll 2015年7月7日
 */
@NoRepositoryBean
public interface IUserDao<T extends IUser<T>> extends BaseDao<T> {
		
	T findByLoginName(String loginName);
	
}
