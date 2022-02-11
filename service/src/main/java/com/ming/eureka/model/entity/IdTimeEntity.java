/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

/**
 * 包含时间插入、更新处理的entity
 *
 * @author lll 2015年8月5日
 */
@Getter
@Setter
@MappedSuperclass
public abstract class IdTimeEntity extends IdEntity implements Serializable {

    @Type(type = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    protected Date createTime;

    @Type(type = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    protected Date updateTime;

    @PrePersist
    private void prePersist() {
        Date now = new Date();
        this.setCreateTime(now);
        this.setUpdateTime(now);
    }

    @PreUpdate
    private void preUpdate() {
        Date now = new Date();
        this.setUpdateTime(now);
    }

}
