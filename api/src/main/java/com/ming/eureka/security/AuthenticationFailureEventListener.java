package com.ming.eureka.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 认证失败监听
 * @author lll
 */
@Slf4j
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent event) {
        final WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails();
        final Object principal = event.getAuthentication().getPrincipal();

        log.info("Failed login for: {}, from: {}", principal, auth.getRemoteAddress());
    }
}
