package com.ming.eureka.restcontroller;

import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页
 */
@Controller
@RequestMapping(value = "/api")
@Api(tags = "api 文档")
public class ApiIndexController {

	/**
	 * 首页跳转到swagger首页
	 * @param request 请求对象
	 * @return api地址
	 */
	@GetMapping(value="")
	public String index(HttpServletRequest request) {
		return "redirect:/swagger-ui.html";
	}
	
}
