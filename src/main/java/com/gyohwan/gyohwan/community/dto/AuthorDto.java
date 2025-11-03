package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.common.domain.User;

public record AuthorDto(
        String nickname,
        boolean isAnonymous,
        boolean isMember
) {

    public static AuthorDto from(User user, boolean isAnonymous, String guestNickname) {
        if (user == null) {
            // 비회원
            return new AuthorDto(guestNickname, false, false);
        } else if (isAnonymous) {
            // 회원 + 익명
            return new AuthorDto("익명", true, true);
        } else {
            // 회원 + 실명
            return new AuthorDto(user.getNickname(), false, true);
        }
    }
}

