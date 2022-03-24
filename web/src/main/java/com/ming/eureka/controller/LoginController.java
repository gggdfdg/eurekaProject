package com.ming.eureka.controller;

import com.ming.eureka.util.SecureUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录控制器
 */
@Controller
public class LoginController extends BaseController {

    /**
     * 登录验证码的key
     */
    private final String SESSION_SYS_ADMIN_SAFE_CODE = "session_sys_admin_safe_code";

    /**
     * 退出
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "logoutsucc")
    public Object logoutsucc() {
        return "login";
    }

    /**
     * 跳转登录界面（如果已经登录过，跳转首页）
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "loginp")
    public Object loginPage() {
        if (SecureUtil.isAuthenticated()) {
            return "redirect:/index";
        }
        return "login";
    }

    /**
     * 基础目录跳转到登录拦截
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    public Object index() {
        return "redirect:/loginp";
    }

    /**
     * 登录后续处理器
     *
     * @param action
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.GET, value = "loginp/{action}")
    public Object loginPage2(@PathVariable String action, HttpServletRequest request, Model model) throws Exception {
        //授权过直接跳转首页
        if (SecureUtil.isAuthenticated()) {
            return "redirect:/index";
        }
        String msg = "账号或者密码错误，请重新输入";
        if (action.equals("error")) {
            //如果security给的错误信息是Bad credentials，就是账号或者密码错误
            if (request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                msg = ((Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION")).getMessage();
                if (msg.equals("Bad credentials")) {
                    msg = "账号或者密码错误，请重新输入";
                }
            }
        } else if (action.equals("expired")) {
            //登录超时
            msg = "登录超时或在其他端登录";
        }
        //将错误信息返回
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

    /**
     * 发起退出请求
     */
    @RequestMapping(method = RequestMethod.GET, value = "outPage")
    public void outPage(HttpServletResponse response) throws IOException{
        this.sendByPost(response,"/logout");
    }

    public void sendByPost(HttpServletResponse response, String url) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println(" <HEAD><TITLE>sender</TITLE></HEAD>");
        out.println(" <BODY>");
        out.println("<form name=\"submitForm\" action=\"" + url + "\" method=\"post\">");
//        Iterator<String> it = HttpClientPostFs.parameter.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            out.println("<input type=\"hidden\" name=\"" + key + "\" value=\"" + HttpClientPostFs.parameter.get(key) + "\"/>");
//        }
        out.println("<input type=\"submit\" " + "\" value=\"" + "按钮提交" + "\"/>");
        out.println("</from>");
//        out.println("<script>window.document.submitForm.submit();</script> ");
        out.println(" </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }
}
