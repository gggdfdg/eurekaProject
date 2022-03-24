package com.ming.eureka.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

@Slf4j
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent event) {
        log.info("Successful login for: {}", event.getAuthentication().getPrincipal().toString());
    }
}
