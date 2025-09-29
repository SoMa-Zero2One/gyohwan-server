package com.gyohwan.gyohwan.auth.controller;

import com.gyohwan.gyohwan.auth.dto.OAuthCodeRequest;
import com.gyohwan.gyohwan.auth.dto.SignInResponse;
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

//    @PostMapping("/apple")
//    public ResponseEntity<OAuthResponse> processAppleOAuth(
//            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
//    ) {
//        OAuthResponse oAuthResponse = appleOAuthService.processOAuth(oAuthCodeRequest);
//        return ResponseEntity.ok(oAuthResponse);
//    }

    @PostMapping("/kakao")
    public ResponseEntity<SignInResponse> processKakaoOAuth(
            @Valid @RequestBody OAuthCodeRequest oAuthCodeRequest
    ) {
        SignInResponse response = kakaoOAuthService.processOAuth(oAuthCodeRequest.code());
        return ResponseEntity.ok(response);
    }
}
