package com.ming.eureka.security;

import com.ming.eureka.model.dao.config.SysConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionStrategy;

/**
 * 安全认证配置
 *
 * @author lll
 */
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties security;
    @Autowired
    private CustomUserDetailsService userservice;
    @Autowired
    private SysConfigDao sysConfigDao;

    @Bean
    public PasswordEncoder passwordEncoder() {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder();
        return encoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.userDetailsService(userservice).passwordEncoder(
                passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().ignoringAntMatchers("/login");
        AuthFailHandler authFailHandler = new AuthFailHandler();
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(
                        "/swagger-ui.html**",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",
                        "/api",
                        "/api/login",
                        "/api/token",
                        "/api/server",
                        "/api/script"
                ).permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .successForwardUrl("/api/token")
                .failureHandler(authFailHandler).permitAll()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessUrl("/api/logoutsucc").permitAll()
                .and().sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/api/expired");
    }

    // Register HttpSessionEventPublisher
    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }


    /**
     * 使用header 作为session策略
     *
     * @return
     */
    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
}
