package com.ming.eureka;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    /**
     * 根据key查询request中的cookie
     *
     * @param key
     * @return Cookie
     */
    public static Cookie getCookieByName(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookieByName(request, name);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public static void deleteCookie(HttpServletResponse response, String cookieName, String path) {
        Cookie newCookie = new Cookie(cookieName, null);
        newCookie.setMaxAge(0);
        newCookie.setPath(path);
        response.addCookie(newCookie);
    }


}
