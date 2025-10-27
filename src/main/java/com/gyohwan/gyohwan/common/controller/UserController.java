package com.gyohwan.gyohwan.common.controller;

import com.gyohwan.gyohwan.common.dto.*;
import com.gyohwan.gyohwan.common.service.SchoolEmailService;
import com.gyohwan.gyohwan.common.service.UserService;
import com.gyohwan.gyohwan.security.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SchoolEmailService schoolEmailService;
    private final CookieUtil cookieUtil;

    @GetMapping("/me")
    public MyUserResponse getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUser(userId);
    }

    @GetMapping("/me/gpas")
    public UserGpaResponse getMyGpas(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUserGpas(userId);
    }

    @PostMapping("/me/gpas")
    public GpaResponse createMyGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateGpaRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.createGpa(userId, request);
    }

    @GetMapping("/me/languages")
    public UserLanguageResponse getMyLanguages(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.findUserLanguages(userId);
    }

    @PostMapping("/me/languages")
    public LanguageResponse createMyLanguage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateLanguageRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return userService.createLanguage(userId, request);
    }

    @PostMapping("/me/password")
    public ChangePasswordResponse changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.changePassword(userId, request.currentPassword(), request.newPassword());
        return new ChangePasswordResponse("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/me/school-email")
    public ResponseEntity<SchoolEmailResponse> requestSchoolEmailVerification(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SchoolEmailRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String schoolEmail = schoolEmailService.requestSchoolEmailVerification(userId, request.schoolEmail());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new SchoolEmailResponse(schoolEmail));
    }

    @PostMapping("/me/school-email/confirm")
    public ResponseEntity<SchoolEmailResponse> confirmSchoolEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SchoolEmailConfirmRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String schoolEmail = schoolEmailService.confirmSchoolEmail(userId, request.code());
        return ResponseEntity.ok(new SchoolEmailResponse(schoolEmail));
    }

    @DeleteMapping("/me/withdraw")
    public ResponseEntity<WithdrawResponse> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletResponse response
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        // 회원 탈퇴 처리
        userService.deleteUser(userId);

        // 쿠키 삭제
        cookieUtil.deleteAccessTokenCookie(response);
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(new WithdrawResponse("회원탈퇴가 완료되었습니다."));
    }
}
