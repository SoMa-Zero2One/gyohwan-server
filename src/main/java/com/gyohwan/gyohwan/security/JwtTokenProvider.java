package com.gyohwan.gyohwan.security;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long expirationTimeMs;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;

    private String issuer = "gyohwan.com";

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-time-ms}") long expirationTimeMs,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTimeMs = expirationTimeMs;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    public String generateAccessToken(Authentication authentication) {
        String userId = authentication.getName();
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 서명 키
                .compact(); // 토큰 생성
    }

    /**
     * 사용자 정보를 기반으로 Access Token을 생성하는 메서드
     */
    public String createToken(Long longUserId) {
        String userId = String.valueOf(longUserId);
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 서명 키
                .compact(); // 토큰 생성
    }

    /**
     * 토큰에서 사용자 ID를 추출하는 메서드
     */
    public String getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String subject = claims.getSubject();
        return subject;
    }

    /**
     * 토큰을 검증하는 메서드
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * 토큰을 복호화하여 Spring Security의 Authentication 객체를 생성하는 메서드
     * 이 메서드는 실제 UserDetailsService와 연동이 필요할 수 있습니다.
     */
    public Authentication getAuthentication(String token) {
        String stringUserId = getIdFromToken(token);
        Long userId = Long.parseLong(stringUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("UserId로 사용자를 찾을 수 없습니다: " + userId));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getId()),
                "", // 토큰 기반 인증이므로 비밀번호는 필요 없습니다.
                Collections.emptyList()
        );
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}