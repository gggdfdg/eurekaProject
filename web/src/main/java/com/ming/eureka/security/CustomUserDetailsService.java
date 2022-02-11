package com.ming.eureka.security;

import javax.servlet.http.HttpServletRequest;

import com.ming.eureka.Constant;
import com.ming.eureka.model.dao.config.SysConfigDao;
import com.ming.eureka.model.entity.config.SysConfig;
import com.ming.eureka.model.entity.sysuser.SysUser;
import com.ming.eureka.model.service.sysuser.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * 登录处理
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

	/**
	 * 用户业务
	 */
	private @Autowired
	SysUserService sysUserService;
	/**
	 * 系统业务
	 */
	private @Autowired SysConfigDao sysConfigDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//找到服务器配置校验服务器是不是被锁了
		SysConfig sysConfig = sysConfigDao.findById((long) 1).orElse(null);
		if(sysConfig==null){
			throw new BadCredentialsException("找不到配置");
		}
		if (sysConfig.getLockStatus() == SysConfig.LOCK_STATUS_LOCK) {
			throw new BadCredentialsException("服务器锁定");
		}
		//校验验证码是不是有问题
		String safecode = getRequest().getParameter("safecode");
		String sessionsafecode = getSafeCodeValues();
		if (safecode != null && !safecode.trim().equals("") && !safecode.toLowerCase().equals(
						sessionsafecode == null ? "" : sessionsafecode.toLowerCase())) {
			throw new BadCredentialsException("验证码错误，请重新输入");
		}
		//获取用户校验用户是不是被锁
		SysUser sysUser = sysUserService.findNormalUser(username);
		if (sysUser == null) {				
			throw new BadCredentialsException("用户不存在，请重新输入");
		}
		if (!sysUser.isEnable()) {
			throw new BadCredentialsException("登录用户名已被锁定，请重新输入");
		}
		//移除验证码的key
		this.getRequest().getSession().removeAttribute(Constant.SESSION_SYS_ADMIN_SAFE_CODE);
		return new CurrentSysUser(sysUser);
	}

	/**
	 * 获取当前dengue验证码内容
	 * 
	 * @return String
	 */
	private String getSafeCodeValues() {
		Object safeCode = this.getRequest().getSession().getAttribute(Constant.SESSION_SYS_ADMIN_SAFE_CODE);
		return safeCode != null ? safeCode.toString() : null;
	}

	/**
	 * 获取上下文HttpServletRequest
	 */
	protected HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		return ((ServletRequestAttributes) attributes).getRequest();
	}
	
}
