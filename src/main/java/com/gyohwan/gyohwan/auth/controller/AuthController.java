package com.gyohwan.gyohwan.auth.controller;

import com.gyohwan.gyohwan.auth.dto.*;
import com.gyohwan.gyohwan.auth.service.EmailAuthService;
import com.gyohwan.gyohwan.auth.service.KakaoOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@RestController
public class AuthController {

    private final KakaoOAuthService kakaoOAuthService;
    private final EmailAuthService emailAuthService;

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
}
