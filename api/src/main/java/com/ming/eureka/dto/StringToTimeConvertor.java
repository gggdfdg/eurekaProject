/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.dto;

import java.sql.Time;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 时间转换
 * @author lll 2015年7月24日
 */
@Component
public class StringToTimeConvertor implements Converter<String, Time> {
	
	@Override
	public Time convert(String source) {
		if (StringUtils.countMatches(source, ":") <= 1) {
			source = source.concat(":00");
		}
		return Time.valueOf(source);
	}
}
