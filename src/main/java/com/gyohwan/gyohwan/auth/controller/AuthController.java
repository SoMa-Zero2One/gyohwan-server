package com.gyohwan.gyohwan.auth.controller;

import com.gyohwan.gyohwan.auth.dto.*;
import com.gyohwan.gyohwan.auth.service.EmailAuthService;
import com.gyohwan.gyohwan.auth.service.GoogleOAuthService;
import com.gyohwan.gyohwan.auth.service.KakaoOAuthService;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final EmailAuthService emailAuthService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

//    @PostMapping("/apple")
//    public ResponseEntity<OAuthResponse> processAppleOAuth(
//            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
//    ) {
//        OAuthResponse oAuthResponse = appleOAuthService.processOAuth(oAuthCodeRequest);
//        return ResponseEntity.ok(oAuthResponse);
//    }

    @PostMapping("/login/social/kakao")
    public ResponseEntity<TokenResponse> processKakaoOAuth(
            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
    ) {
        TokenResponse response = kakaoOAuthService.processOAuth(oAuthCodeRequest.code());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/social/google")
    public ResponseEntity<TokenResponse> processGoogleOAuth(
            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
    ) {
        TokenResponse response = googleOAuthService.processOAuth(oAuthCodeRequest.code());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/email")
    public ResponseEntity<TokenResponse> processEmailLogin(
            @Valid @RequestBody EmailLoginRequest request
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
            @Valid @RequestBody EmailConfirmRequest request
    ) {
        String accessToken = emailAuthService.confirmEmail(request.email(), request.code());
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @GetMapping("/signup/email/check")
    public ResponseEntity<EmailCheckResponse> checkEmailExists(
            @RequestParam String email
    ) {
        boolean exists = emailAuthService.checkEmailExists(email);
        return ResponseEntity.ok(new EmailCheckResponse(exists));
    }
}
