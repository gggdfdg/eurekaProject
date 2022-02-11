package com.ming.eureka.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhujianming
 * @date 2022-02-07 18:13
 */
@RequestMapping("")
@Controller
public class DemoController {

    /**
     * ajax返回json
     * @return
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
     * @return
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

