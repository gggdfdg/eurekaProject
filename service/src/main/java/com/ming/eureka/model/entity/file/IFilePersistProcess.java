/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity.file;

import java.util.Map;

/**
 * 文件持久化额外处理接口
 * @author lll 2015年7月15日
 */
public interface IFilePersistProcess {
	
	/**
	 * 可唯一标示实体的查询参数（用于修改），插入则直接返回空
	 * 		key:属性名  value:匹配值
	 * @return
	 */
	Map<String, Object> searchParams();
	
	/**
	 * 持久化额外处理
	 * @param file
	 * @param sourceType
	 */
	void persistProcess(AbstractFile file, SourceType sourceType);
}
