package com.gyohwan.gyohwan.auth.dto;

/**
 * Redis에 저장될 비밀번호 재설정 인증 정보
 */
public record PasswordResetVerificationInfo(
        String email,
        String verificationCode
) {
}






