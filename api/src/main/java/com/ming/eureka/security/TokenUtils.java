package com.ming.eureka.security;

import com.ming.eureka.dto.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@Component //SpringBoot中组件类需要使用@Component进行组件注册才能使用
public class TokenUtils {

    /**
     * 由字符串生成加密key
     *
     * @return
     */
    public static SecretKey generalKey() {
        String stringKey = "thisisasecretkey"; //随机写的
        // 本地的密码解码
        byte[] encodedKey = Base64.decodeBase64(stringKey);
        // 根据给定的字节数组使用AES加密算法构造一个密钥
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 生成JWT
     *
     * @return
     */
    public static String createToken(String userName) {
        //设置JWT的header
        HashMap<String, Object> map = new HashMap<String, Object>();
        //Head
        map.put("alg", "HS256");
        map.put("typ", "jwt");
        //Payload
        map.put("username", userName); //根据userName生成jwt
        //设置JWT的过期时间
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 20);//当前时间+20mins
        Date expireDate = now.getTime();//Calendar转Date
        //设置JWT生效时间
        Date nowDate = new Date();//系统当前时间
        SecretKey key = generalKey(); //密钥（服务端专有，面向客户端隐藏）
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(map)
                .setExpiration(expireDate)
                .setNotBefore(nowDate)
                //Signature
                .signWith(SignatureAlgorithm.HS256, key);//设置签发算法和密钥
        return Constant.TOKEN_PERFIX + jwtBuilder.compact();//jwt前面一般会加上Bearer
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public static Claims parseToken(String token) {
        SecretKey key = generalKey();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token.replace(Constant.TOKEN_PERFIX, "")).getBody();//TOKEN_PERFIX = "Bearer"
            return claims;
        } catch (Exception e) {
            throw new IllegalStateException("Invalid token." + e.getMessage());
        }
    }
}
