package com.ming.eureka.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.DigestUtils;


/**
 * security安全配置
 */
@Order(10)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 登录业务处理和校验
     */
    private @Autowired
    CustomUserDetailsService userDetailsService;

    public static final String REMEMBER_ME_KEY = "213^$%%^$djask";

    /**
     * 登录token记录
     */
    private @Autowired
    PersistentTokenRepository redisTokenRepository;

    /**
     * 登录成功处理
     */
    private @Autowired
    AuthSuccessHandler authSuccessHandler;

    /**
     * security配置
     */
    private @Autowired
    SecurityProperties security;

    /**
     * session的key
     */
    private final String SessionName = "SYS_SPRING_SECURITY_CONTEXT";

    /**
     * security加密使用的算法
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    public static void main(String args[]){
        String encodeStr = DigestUtils.md5DigestAsHex("abc1233".getBytes());
        System.out.println(BCrypt.hashpw(encodeStr, BCrypt.gensalt()));
    }

    /**
     * security配置加密的方式和登录处理类
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(
                passwordEncoder());
    }

    /**
     * security关于访问路径的权限设置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // TODO Auto-generated method stub
        http.csrf().ignoringAntMatchers("/logout");
        //全局session，如果用户信息变化，本session也会变化。
        HttpSessionSecurityContextRepository httpSecurityRepository = new HttpSessionSecurityContextRepository();
        httpSecurityRepository.setDisableUrlRewriting(false);
        httpSecurityRepository.setAllowSessionCreation(true);
        httpSecurityRepository.setSpringSecurityContextKey(SessionName);
        http.setSharedObject(SecurityContextRepository.class,
                httpSecurityRepository);
        http
                .authorizeRequests()
                .antMatchers("/loginp/**", "/script/**", "/images/**", "/style/**", "/error/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/loginp")
                .loginProcessingUrl("/login")
                .successHandler(authSuccessHandler)
                .failureForwardUrl("/loginp/error")
                .failureUrl("/loginp/error")
                .permitAll()
                .and()
                .rememberMe()
                .key(REMEMBER_ME_KEY)
                .rememberMeServices(rememberMeServices())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logoutsucc").permitAll()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/loginp/expired");
    }

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 记得我的服务
     * 记住登录配置
     *
     * @return {@link PersistentTokenBasedRememberMeServices}
     */
    @Bean
    public PersistentTokenBasedRememberMeServices rememberMeServices() {
        PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices
                = new PersistentTokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService, redisTokenRepository);
        persistentTokenBasedRememberMeServices.setUseSecureCookie(false);
        persistentTokenBasedRememberMeServices.setAlwaysRemember(true);
        //token过期时间7天
        persistentTokenBasedRememberMeServices.setTokenValiditySeconds(7*24*60*60);
        return persistentTokenBasedRememberMeServices;
    }
}
