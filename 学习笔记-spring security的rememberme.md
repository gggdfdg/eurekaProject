# 学习笔记-spring security的rememberme
 
------

首先介绍下本章学习的内容：
 
> * 教你如何用remember me功能

## 教你如何用remember me功能
remember me是登录后记住我们的功能，可以解决重启后，会话失效的问题。
之前登录后的jessionid是存在游览器上面，可以拿着这个去获取会话，服务器获取到后知道用户已经登录了，接下来的操作就不用校验登录状态
但是服务器重启后，游览器拿着jessionid也没办法获取到登录状态，因为服务器session是放在内存中的，重启后都失效了。
现在remember me将session放在redis中，并且可以设置失效时间，服务器重启几万次都能正确拿到用户会话信息

接下来我们操作如何失效这种功能

```
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
```
重要的是下面这几个，开启rememberme功能，并设置存在redis中的key和策略
```
.rememberMe()
.key(REMEMBER_ME_KEY)
.rememberMeServices(rememberMeServices())
```

接下来看看策略

```
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
```
这里设置了七天失效，接下来看看redis的配置

    ```
    /**
     * redis模板
     *
     * @param redisConnectionFactory 能连接工厂
     * @return {@link RedisTemplate<Object, Object>}
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }
    
    /**
     * redis仓库
     *
     * @param redisTemplate redis模板
     * @return {@link RedisTokenRepositoryImpl}
     */
    @Bean("redisTokenRepository")
    public RedisTokenRepositoryImpl redisTokenRepository(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisTokenRepositoryImpl(redisTemplate);
    }
    ```
 
上面写了redis的配置，接下来看看redis配合remember me的类处理

```
package com.ming.eureka.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 存储remember me token
 *
 * @author lll
 */
public class RedisTokenRepositoryImpl implements PersistentTokenRepository {

    /**
     * 复述,模板
     */
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 复述,令牌库impl
     *
     * @param redisTemplate 复述,模板
     */
    public RedisTokenRepositoryImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 创建新的令牌
     *
     * @param token 令牌
     */
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        removeUserTokens(token.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken:series:" + token.getSeries(), token.getUsername());
        map.put("accessToken:users:" + token.getUsername(), token.getSeries());
        map.put("accessToken:tokens:" + token.getSeries(), new PersistentAccessToken(token));
        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.expire("accessToken:series:" + token.getSeries(),7, TimeUnit.DAYS);//设置token保存期限
        redisTemplate.expire("accessToken:users:" + token.getUsername(),7, TimeUnit.DAYS);//设置token保存期限
        redisTemplate.expire("accessToken:tokens:" + token.getSeries(),7, TimeUnit.DAYS);//设置token保存期限
    }

    /**
     * 更新的令牌
     *
     * @param series     系列
     * @param tokenValue 令牌值
     * @param lastUsed   最后使用
     */
    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        ValueOperations<Object, Object> opsForValue = redisTemplate.opsForValue();
        String username = (String) opsForValue.get("accessToken:series:" + series);
        PersistentAccessToken token = new PersistentAccessToken(username, series, tokenValue, lastUsed);
        opsForValue.set("accessToken:tokens:" + series, new PersistentAccessToken(token));
    }

    /**
     * 获得令牌系列
     *
     * @param seriesId 系列id
     * @return {@link PersistentRememberMeToken}
     */
    @SuppressWarnings("unchecked")
    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        List<Object> result = redisTemplate.opsForValue().multiGet(
                Arrays.asList(new String[]{"accessToken:series:" + seriesId, "accessToken:tokens:" + seriesId}));
        Map<String, Object> map = (Map<String, Object>) result.get(1);

        PersistentRememberMeToken token = null;
        try {
            token = new PersistentRememberMeToken((String) result.get(0),
                    (String) map.get("series"),
                    (String) map.get("tokenValue"),
                    new Date((Long) map.get("date")));
        } catch (Exception e) {
            return null;
        }
        return token;
    }

    /**
     * 删除用户令牌
     *
     * @param username 用户名
     */
    @Override
    public void removeUserTokens(String username) {
        String series = (String) redisTemplate.opsForValue().get("accessToken:users:" + username);
        redisTemplate.delete(Arrays.asList(new String[]{
                "accessToken:series:" + series,
                "accessToken:users:" + username,
                "accessToken:tokens:" + series}));
    }
}
```