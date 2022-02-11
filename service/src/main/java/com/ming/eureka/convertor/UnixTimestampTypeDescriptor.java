/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.convertor;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import java.sql.*;
import java.util.Date;

/**
 * @author lll 2015年11月9日
 */
public class UnixTimestampTypeDescriptor implements SqlTypeDescriptor {

    private static final long serialVersionUID = 8968921263158668629L;
    public static final UnixTimestampTypeDescriptor INSTANCE = new UnixTimestampTypeDescriptor();

    @Override
    public int getSqlType() {
        return Types.BIGINT;
    }

    @Override
    public boolean canBeRemapped() {
        return true;
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                Date date = javaTypeDescriptor.unwrap(value, Date.class, options);
                st.setLong(index, date.getTime());
            }

            @Override
            protected void doBind(CallableStatement arg0, X arg1, String arg2,
                                  WrapperOptions arg3) throws SQLException {
                // TODO Auto-generated method stub

            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this) {
            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                Date date = new Date(rs.getLong(name));
                return javaTypeDescriptor.wrap(date, options);
            }

            @Override
            protected X doExtract(CallableStatement arg0, int arg1,
                                  WrapperOptions arg2) throws SQLException {
                return null;
            }

            @Override
            protected X doExtract(CallableStatement arg0, String arg1,
                                  WrapperOptions arg2) throws SQLException {
                return null;
            }

        };
    }
}