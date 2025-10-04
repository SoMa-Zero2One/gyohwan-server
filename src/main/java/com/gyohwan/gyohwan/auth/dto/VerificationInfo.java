package com.gyohwan.gyohwan.auth.dto;

public record VerificationInfo(
        String code,
        String hashedPassword
) {
}