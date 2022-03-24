package com.ming.eureka.restcontroller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * demo示范
 * @author zhujianming
 * @date 2022-02-07 18:13
 */
@RequestMapping("")
@Controller
public class DemoController {

    /**
     * ajax返回json
     *
     * @return json实体
     */
    @RequestMapping("/z")
    @ResponseBody
    public Object toJson1() {
        System.out.println("zzzz");
        Map<Integer, Integer> xx = new HashMap<>();
        xx.put(1, 2);
        return xx;
    }

    /**
     * ajax返回json
     * @param response 返回实体
     */
    @RequestMapping("/t")
    @ResponseBody
    public void toJson2(HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.getWriter().write("{\"json\":\"demo\"}");
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

