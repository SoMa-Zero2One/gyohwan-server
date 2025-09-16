package com.gyohwan.gyohwan.legacyYu.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegistrationResponse {

    private Long userId;
    private String uuid;
    private String nickname;
    private String message;

    public static UserRegistrationResponse success(Long userId, String uuid, String nickname) {
        return UserRegistrationResponse.builder()
                .userId(userId)
                .uuid(uuid)
                .nickname(nickname)
                .message("회원가입이 성공적으로 완료되었습니다.")
                .build();
    }
}
