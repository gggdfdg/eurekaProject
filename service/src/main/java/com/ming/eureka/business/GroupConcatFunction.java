/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.business;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * 增加 group_concat 函数
 * 
 * @author lll 2016年10月28日
 */
public class GroupConcatFunction implements SQLFunction {
	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping)
			throws QueryException {

		return StandardBasicTypes.STRING;
	}

	@Override
	public String render(Type firstArgumentType, List arguments,
			SessionFactoryImplementor factory) throws QueryException {

		StringBuilder sb = new StringBuilder();
		int size = arguments.size();
		if (size < 1) {
			throw new QueryException(new IllegalArgumentException(
					"group_concat shoudl have one arg"));
		}
		sb.append("group_concat(");
		
		Object argument = null;
		for (int i = 0; i < size; i++) {
			argument = arguments.get(i);
			sb.append(argument);
			if (i != size - 1) {
				sb.append(",");
			}
		}

		sb.append(")");

		return sb.toString();
	}
}
