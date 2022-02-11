package com.ming.eureka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * @author lazier
 */
@ControllerAdvice
public class CurrentUserControllerAdvice {

    @Autowired
    private ResourceUrlProvider resourceUrlProvider;

    @Value("${sys.config.name}")
    private String sysName;

    @Value("${sys.config.logo.url}")
    private String sysImgUrl;

    @ModelAttribute("sysName")
    public String getSysName() {
        return this.sysName;
    }

    @ModelAttribute("sysImgUrl")
    public String getSysImgUrl() {
        return this.sysImgUrl;
    }

    @ModelAttribute("urls")
    public ResourceUrlProvider urls() {
        return this.resourceUrlProvider;
    }


}