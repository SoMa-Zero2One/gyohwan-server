package com.gyohwan.gyohwan.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authz -> authz
                                // auth
                                .requestMatchers("/v1/auth/**").permitAll()
                                // article
                                .requestMatchers("/v1/article-groups/**").permitAll()
                                .requestMatchers("/v1/articles/**").permitAll()
                                // compare
                                .requestMatchers("/v1/seasons/*/my-application").authenticated()
                                .requestMatchers("/v1/seasons/*/eligibility").authenticated()
                                .requestMatchers(HttpMethod.POST, "/v1/seasons/*").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/v1/seasons/*").authenticated()
                                .requestMatchers("/v1/seasons/**").permitAll()
                                .requestMatchers("/v1/slots/**").permitAll()
                                .requestMatchers("/v1/applications/**").authenticated()
                                // user
                                .requestMatchers("/v1/users/me/**").authenticated()
                                // community
                                .requestMatchers("/v1/community/**").permitAll()
                                // 그 외
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin 설정 (개발환경과 프로덕션 환경에 맞게 수정 필요)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080",
                "https://yu.gyohwan.com",
                "https://inu.gyohwan.com",
                "https://gyohwan.com",
                "https://www.gyohwan.com",
                "https://test.gyohwan.com"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*", "Authorization", "Content-Type", "X-Requested-With", "Accept"));

        // 자격 증명(쿠키, Authorization 헤더 등) 허용
        configuration.setAllowCredentials(true);

        // preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}