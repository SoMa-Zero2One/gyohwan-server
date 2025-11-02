package com.gyohwan.gyohwan.auth.controller;

import com.gyohwan.gyohwan.auth.dto.*;
import com.gyohwan.gyohwan.auth.service.EmailAuthService;
import com.gyohwan.gyohwan.auth.service.GoogleOAuthService;
import com.gyohwan.gyohwan.auth.service.KakaoOAuthService;
import com.gyohwan.gyohwan.auth.service.PasswordResetService;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.security.CookieUtil;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@RestController
@Slf4j
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final EmailAuthService emailAuthService;
    private final PasswordResetService passwordResetService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

//    @PostMapping("/apple")
//    public ResponseEntity<OAuthResponse> processAppleOAuth(
//            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
//    ) {
//        OAuthResponse oAuthResponse = appleOAuthService.processOAuth(oAuthCodeRequest);
//        return ResponseEntity.ok(oAuthResponse);
//    }

    @PostMapping("/login/social/kakao")
    public ResponseEntity<TokenResponse> processKakaoOAuth(
            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = kakaoOAuthService.processOAuth(oAuthCodeRequest.code());
        // 쿠키에 JWT 저장
        cookieUtil.addAccessTokenCookie(response, tokenResponse.accessToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login/social/google")
    public ResponseEntity<TokenResponse> processGoogleOAuth(
            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = googleOAuthService.processOAuth(oAuthCodeRequest.code());
        // 쿠키에 JWT 저장
        cookieUtil.addAccessTokenCookie(response, tokenResponse.accessToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login/email")
    public ResponseEntity<TokenResponse> processEmailLogin(
            @Valid @RequestBody EmailLoginRequest request,
            HttpServletResponse response
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // 인증 성공 시 SecurityContext 에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 생성
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            TokenResponse tokenResponse = new TokenResponse(accessToken);

            // 쿠키에 JWT 저장
            cookieUtil.addAccessTokenCookie(response, accessToken);

            return ResponseEntity.ok(tokenResponse);
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorCode.EMAIL_LOGIN_FAILED);
        }
    }

    @PostMapping("/signup/email")
    public ResponseEntity<EmailSignupResponse> processEmailSignup(
            @Valid @RequestBody EmailSignupRequest request
    ) {
        String email = emailAuthService.requestEmailVerification(request.email(), request.password());
        return ResponseEntity.ok(new EmailSignupResponse(email));
    }

    @PostMapping("/signup/email/confirm")
    public ResponseEntity<TokenResponse> processEmailSignupConfirm(
            @Valid @RequestBody EmailConfirmRequest request,
            HttpServletResponse response
    ) {
        String accessToken = emailAuthService.confirmEmail(request.email(), request.code());
        // 쿠키에 JWT 저장
        cookieUtil.addAccessTokenCookie(response, accessToken);
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @GetMapping("/signup/email/check")
    public ResponseEntity<EmailCheckResponse> checkEmailExists(
            @RequestParam String email
    ) {
        boolean exists = emailAuthService.checkEmailExists(email);
        return ResponseEntity.ok(new EmailCheckResponse(exists));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse response) {
        // 쿠키 삭제
        cookieUtil.deleteAccessTokenCookie(response);
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new LogoutResponse("로그아웃되었습니다."));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<PasswordResetResponse> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDto request
    ) {
        log.info("비밀번호 재설정 요청 API 호출. Email: {}", request.email());
        String email = passwordResetService.requestPasswordReset(request.email());
        return ResponseEntity.ok(new PasswordResetResponse(email, "비밀번호 재설정 인증 코드가 이메일로 발송되었습니다."));
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<PasswordResetConfirmResponse> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request
    ) {
        log.info("비밀번호 재설정 확인 API 호출. Email: {}", request.email());
        passwordResetService.confirmPasswordReset(request.email(), request.code(), request.newPassword());
        return ResponseEntity.ok(new PasswordResetConfirmResponse("비밀번호가 성공적으로 변경되었습니다."));
    }
}
