package com.ming.eureka.controller;

import com.ming.eureka.util.SecureUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController extends BaseController {

    private final String SESSION_SYS_ADMIN_SAFE_CODE = "session_sys_admin_safe_code";

    @RequestMapping(method = RequestMethod.GET, value = "logoutsucc")
    public Object logoutsucc(HttpServletRequest request) throws Exception {
        removeSysUserSession();
        return "login";
    }

    @RequestMapping(method = RequestMethod.GET, value = "loginp")
    public Object loginPage(HttpServletRequest request, Model model) throws Exception {
        if (SecureUtil.isAuthenticated()) {
            return "redirect:/index";
        }
        return "login";
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    public Object index(HttpServletRequest request, Model model) throws Exception {
        return "redirect:/loginp";
    }

    @RequestMapping(method = RequestMethod.GET, value = "loginp/{action}")
    public Object loginPage2(@PathVariable String action, HttpServletRequest request, Model model) throws Exception {
        if (SecureUtil.isAuthenticated()) {
            return "redirect:/index";
        }
        String msg = "账号或者密码错误，请重新输入";
        if (action.equals("error")) {
            if (request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                msg = ((Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage();
                if (msg.equals("Bad credentials")) {
                    msg = "账号或者密码错误，请重新输入";
                }
            }
        } else if (action.equals("expired")) {
            msg = "登录超时或在其他端登录";
        }
        model.addAttribute("msg", msg);
        return "login";
    }

    /**
     * 获取二维码图片
     */
    @RequestMapping(method = RequestMethod.GET, value = "loginp/getSafeCode")
    @ResponseBody
    public void getSafeCode() {
        this.renderValidateImg(SESSION_SYS_ADMIN_SAFE_CODE);
    }
}
