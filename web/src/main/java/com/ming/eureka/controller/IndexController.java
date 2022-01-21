package com.ming.eureka.controller;

import com.ming.eureka.ExcelUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhujianming
 * @date 2022-01-13 14:36
 */
@RequestMapping("")
@Controller
public class IndexController {
    @RequestMapping("/")
    @ResponseBody
    public Object index() {
        System.out.println("zzzz");
        Map<Integer, Integer> xx = new HashMap<>();
        xx.put(1, 2);
        return xx;
    }

    @RequestMapping("/t")
    @ResponseBody
    public void index(HttpServletResponse response) {
        System.out.println("Gsgs");
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.getWriter().write("gsagsagsgsgsg");
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/e")
    @ResponseBody
    public void e(HttpServletResponse response) throws Exception {
        List<zz> test = new ArrayList<>();
        test.add(zz.fsf("445464", "阿肥发"));
        test.add(zz.fsf("0000", "主牌1"));
        test.add(zz.fsf("7888", "猪排1"));
        List<String> names = new ArrayList<>();
        names.add("狗发名");
        names.add("狗发密码");
        List<String> pro = new ArrayList<>();
        pro.add("username");
        pro.add("password");
        ExcelUtil.exportXlstoResponse(response, "脚注名", test, names, pro, "狗发文件");
    }

}
