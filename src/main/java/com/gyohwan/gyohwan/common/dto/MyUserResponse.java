package com.gyohwan.gyohwan.common.dto;

import com.gyohwan.gyohwan.common.domain.*;

import java.util.Optional;

public record MyUserResponse(
        Long userId,
        String email,
        String schoolEmail,
        String nickname,
        String domesticUniversity,
        Boolean schoolVerified,
        LoginType loginType,
        SocialType socialType
) {
    public static MyUserResponse from(User user) {
        return new MyUserResponse(
                user.getId(),
                user.getEmail(),
                user.getSchoolEmail(),
                user.getNickname(),
                Optional.ofNullable(user.getDomesticUniv())
                        .map(DomesticUniv::getName)
                        .orElse(null),
                user.getSchoolVerified(),
                user.getLoginType(),
                Optional.ofNullable(user.getSocial())
                        .map(Social::getSocialCode)
                        .orElse(null)
        );
    }
}
