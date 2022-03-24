package com.ming.eureka.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description="登录成功后返回信息")
public class LoginInfo {
	
	@ApiModelProperty(notes = "socket 地址")
	private String socketHost;
	@ApiModelProperty(notes = "socket 端口")
	private int socketPort;
	@ApiModelProperty(notes = "登录token")
	private String token;
	@ApiModelProperty(notes = "UID过期时间")
	private String expireDate;
	@ApiModelProperty(notes = "分身数量")
	private int clonedCount;
	
}
