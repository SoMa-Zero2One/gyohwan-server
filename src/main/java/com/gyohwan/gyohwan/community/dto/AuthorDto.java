package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.common.domain.User;

public record AuthorDto(
        String nickname,
        boolean isAnonymous,
        boolean isMember,
        boolean isAuthor
) {

    public static AuthorDto from(User user, boolean isAnonymous, String guestNickname, Long userId) {
        if (user == null) {
            // 비회원
            return new AuthorDto(guestNickname, false, false, false);
        } else if (isAnonymous) {
            // 회원 + 익명
            return new AuthorDto("익명", true, true, user.getId().equals(userId));
        } else {
            // 회원 + 실명
            return new AuthorDto(user.getNickname(), false, true, user.getId().equals(userId));
        }
    }
}

