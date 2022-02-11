package com.ming.eureka.redis;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.util.Date;

public class PersistentAccessToken extends PersistentRememberMeToken {

    public PersistentAccessToken(
            PersistentRememberMeToken persistentRememberMeToken) {
        super(persistentRememberMeToken.getUsername(),
                persistentRememberMeToken.getSeries(),
                persistentRememberMeToken.getTokenValue(),
                persistentRememberMeToken.getDate());
    }

    public PersistentAccessToken(String username, String series,
                                 String tokenValue, Date date) {
        super(username, series, tokenValue, date);
    }
}
