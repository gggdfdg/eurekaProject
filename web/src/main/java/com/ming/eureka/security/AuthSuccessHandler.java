package com.ming.eureka.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ming.eureka.CommonUtil;
import com.ming.eureka.GeolocationUtils;
import com.ming.eureka.model.entity.sysuser.SysUser;
import com.ming.eureka.model.service.sysuser.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 浏览器/Api 认证成功跳转处理
 * 
 * @author lee 2016年8月21日
 */
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 用户信息查询服务对象
     */
    @Autowired
    private SysUserService sysUserService;
    
    /**
     * 登录成功后执行
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 更新上次登录信息
        SysUser sysUser = this.sysUserService.findNormalUser(authentication.getName());
        sysUser.setLastLoginIp(CommonUtil.getClientIp(request));
        sysUser.setLastLoginDate(new Date());

        // 保存登录地址
        if (sysUser.getLastLoginIp().equals(CommonUtil.LOCAL_IP)) {
            sysUser.setLastLoginAddress("本地访问");
        } else if (sysUser.getLastLoginIp().startsWith("192.168")) {
            sysUser.setLastLoginAddress("局域网访问");
        } else {
            sysUser.setLastLoginAddress(GeolocationUtils.getLocation(sysUser.getLastLoginIp()).getAddress());
        }
        sysUser.setUpdateTime(new Date());
        try {
            sysUserService.save(sysUser);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

        response.sendRedirect("/index");
    }
}
