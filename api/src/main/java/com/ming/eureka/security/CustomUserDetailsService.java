package com.ming.eureka.security;

import com.ming.eureka.model.dao.config.SysConfigDao;
import com.ming.eureka.model.entity.config.SysConfig;
import com.ming.eureka.model.entity.user.User;
import com.ming.eureka.model.service.user.UserService;
import com.ming.eureka.util.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private @Autowired
    UserService userService;
    private @Autowired
    SysConfigDao sysConfigDao;

    @Override
    public UserDetails loadUserByUsername(String loginName)
            throws UsernameNotFoundException {

        SysConfig sysConfig = sysConfigDao.findById((long) 1).orElse(null);
        if (sysConfig == null) {
            throw new BadCredentialsException("找不到服务器配置");
        }
        if (sysConfig.getLockStatus() == SysConfig.LOCK_STATUS_LOCK) {
            throw new BadCredentialsException("服务器锁定");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 设备登录
        if (SecureUtil.isApi()) {
            User user = userService.findUserByLoginName(loginName);
            if (user == null) {
                throw new BadCredentialsException("员工不存在");
            }
            if (!user.isEnable()) {
                throw new BadCredentialsException("登录员工名已被锁定");
            }
            authorities.add(new SimpleGrantedAuthority("R_ALL"));
            return new org.springframework.security.core.userdetails.User(
                    loginName, user.getPassword(), authorities);
        } else {
            throw new BadCredentialsException("非法登录");
        }
    }

}
