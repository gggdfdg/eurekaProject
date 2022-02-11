/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity;

import com.ming.eureka.convertor.UnixTimestampType;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 统一定义id的entity基类.
 * <p>
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略.
 * Oracle需要每个Entity独立定义id的SEQUCENCE时，不继承于本类而改为实现一个Idable的接口。
 *
 * @author calvin
 */
// JPA 基类的标识
@Data
@MappedSuperclass
@TypeDef(name = "date", typeClass = UnixTimestampType.class)
public abstract class IdEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    public IdEntity() {
    }

    public IdEntity(Long id) {
        super();
        this.id = id;
    }

    /**
     * 插入前数据初始化 (主要用于设置默认值，如日期等对象的初始化)
     */
    @PrePersist
    public abstract void prepareForInsert();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).toString();
    }

}
