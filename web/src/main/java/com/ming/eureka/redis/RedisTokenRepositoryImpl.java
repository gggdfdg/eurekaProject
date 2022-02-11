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
