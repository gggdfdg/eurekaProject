/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.convertor;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcTimeTypeDescriptor;

import java.util.Date;

/**
 * @author lll 2015年11月9日
 */
public class UnixTimestampType extends AbstractSingleColumnStandardBasicType<Date> implements IdentifierType<Date>, LiteralType<Date> {
    private static final long serialVersionUID = 1L;
    public static final UnixTimestampType INSTANCE = new UnixTimestampType();

    public UnixTimestampType() {
        super(UnixTimestampTypeDescriptor.INSTANCE, JdbcTimeTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{getName(), Date.class.getName()};
    }

    @Override
    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        final Date jdbcDate = Date.class.isInstance(value) ? (Date) value : new Date(value.getTime());
        return StringType.INSTANCE.objectToSQLString(jdbcDate.toString(), dialect);
    }

    @Override
    public Date stringToObject(String xml) {
        return fromString(xml);
    }
}