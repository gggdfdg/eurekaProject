/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity.file;

import javax.annotation.PostConstruct;

import com.ming.eureka.FileUtil;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置文件
 * 
 * @author lll 2015年6月4日
 */
@Component
@Data
public class ConfigInfo {
	@Value("${sms.server.sendurl}")
	private String sendurl;

	@Value("${sms.server.account}")
	private String account;

	@Value("${sms.server.password}")
	private String password;

	@Value("${sms.server.timeout}")
	private int smsTimeout;

	/** 上传文件路径 */
	@Value("${files.pro.path}")
	private String proFilePath;
	
	/** 素材压缩目录 */
	@Value("${files.sns.zip.path}")
	private String snsZipPath;
	
	@Value("${excel.export.path}")
	private String excelFilePath;

	@Value("${excel.import.path}")
	private String excelImportPath;
	
	@Value("${script.export.path}")
	private String scriptExportPath;

	@Value("${files.tem.path}")
    private String temFilePath;

	@PostConstruct
	public void initPath() {
		FileUtil.createFolder(snsZipPath);
		FileUtil.createFolder(proFilePath);
		FileUtil.createFolder(excelFilePath);
		FileUtil.createFolder(excelImportPath);
		FileUtil.createFolder(scriptExportPath);
        FileUtil.createFolder(temFilePath);
	}

}
