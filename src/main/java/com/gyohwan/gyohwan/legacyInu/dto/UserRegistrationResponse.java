package com.gyohwan.gyohwan.legacyInu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegistrationResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String uuid;
    private String nickname;

    public static UserRegistrationResponse success(Long userId, String uuid, String nickname) {
        return new UserRegistrationResponse(true, "회원가입이 성공적으로 완료되었습니다.", userId, uuid, nickname);
    }

    public static UserRegistrationResponse failure(String message) {
        return new UserRegistrationResponse(false, message, null, null, null);
    }
}
