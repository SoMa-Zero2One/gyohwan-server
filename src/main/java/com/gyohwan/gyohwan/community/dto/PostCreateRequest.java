package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.community.domain.Post;


public record PostCreateRequest(
        String title,
        String content,
        boolean isAnonymous,
        String guestNickname,
        String guestPassword,
        String countryCode,
        Long outgoingUnivId
) {

    public Post toEntity(User user) {
        return new Post(
                this.title,
                this.content,
                user,
                this.isAnonymous,
                null,
                null,
                this.countryCode,
                this.outgoingUnivId
        );
    }

    public Post toEntity(String encodedPassword) {
        return new Post(
                this.title,
                this.content,
                null,
                false,
                "익명",
                encodedPassword,
                this.countryCode,
                this.outgoingUnivId
        );
    }
}