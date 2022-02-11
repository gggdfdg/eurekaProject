/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.business;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * 增加 group_concat
 * @author lll 2016年10月28日
 */
public class CustomMySQL5Dialect extends MySQL5Dialect {
	public CustomMySQL5Dialect() {
        super();
        registerFunction("group_concat", new GroupConcatFunction());
    }
}
