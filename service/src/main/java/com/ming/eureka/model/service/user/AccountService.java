/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.service.user;

import com.ming.eureka.BaseService;
import com.ming.eureka.model.dao.user.IUserDao;
import com.ming.eureka.model.entity.user.IUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 用户管理类.
 *
 * @author calvin
 */
public abstract class AccountService<T extends IUser<T>> extends BaseService {

    private static Logger logger = LoggerFactory.getLogger(AccountService.class);

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public abstract IUserDao<T> getUserDao();

    public List<T> getAllUser() {
        return (List<T>) this.getUserDao().findAll();
    }

    public T getUser(Long id) {
        return (T) this.getUserDao().findById(id).orElse(null);
    }

    public T findUserByLoginName(String loginName) {
        return (T) this.getUserDao().findByLoginName(loginName);
    }

    @Transactional(readOnly = false)
    public T registerUser(T iUser) {
        entryptPassword(iUser);
        return this.getUserDao().save(iUser);
    }

    @Transactional(readOnly = false)
    public void updateUser(T user) {
        if (StringUtils.isNotBlank(user.getPlainPassword())) {
            entryptPassword(user);
        }
        this.getUserDao().save(user);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {
        this.getUserDao().deleteById(id);
    }

    /**
     * 验证旧密码
     */
    public boolean validPassword(T user, String oldPassword) {
        return encoder.matches(oldPassword, user.getPassword());
    }

    /**
     *
     */
    protected void entryptPassword(T user) {
        user.setPassword(encoder.encode(user.getPlainPassword()));
    }

}
