package com.ming.eureka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class ErrorController {
	
	@RequestMapping(method = RequestMethod.GET, value = "")
	public String loginp() {
		return "redirect:loginp";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "401")
	public String notauth() {
		return "error/401";
	}
		
	@RequestMapping(method = RequestMethod.GET, value = "404")
	public String notfound() {
		return "error/404";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "500")
	public String error() {
		return "error/500";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "403")
	public String forbidden() {
		return "error/403";
	}
	
}
