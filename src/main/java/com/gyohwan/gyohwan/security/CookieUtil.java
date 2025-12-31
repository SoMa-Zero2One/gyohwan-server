package com.gyohwan.gyohwan.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    @Value("${jwt.cookie.secure:false}")
    private boolean secure;

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure); // HTTPS에서만 전송 (production에서 true)
        cookie.setPath("/");
        cookie.setMaxAge(-1); // 세션 쿠키 (브라우저 종료 시 삭제)
        cookie.setAttribute("SameSite", "Lax"); // CSRF 방지

        response.addCookie(cookie);
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 삭제

        response.addCookie(cookie);
    }

    public static String getAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}

