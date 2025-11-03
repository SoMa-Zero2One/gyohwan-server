package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.community.domain.Post;
import jakarta.validation.constraints.NotBlank;


public record PostCreateRequest(
        @NotBlank(message = "제목은 비어 있을 수 없습니다.")
        String title,
        @NotBlank(message = "내용은 비어 있을 수 없습니다.")
        String content,
        Boolean isAnonymous,
        String guestPassword,
        String countryCode,
        Long outgoingUnivId
) {

    public Post toEntity(User user) {
        boolean isAnonymous = false;
        if (this.isAnonymous != null && this.isAnonymous) {
            isAnonymous = true;
        }
        return new Post(
                this.title,
                this.content,
                user,
                isAnonymous,
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