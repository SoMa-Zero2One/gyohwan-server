package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.community.domain.Comment;
import com.gyohwan.gyohwan.community.domain.Post;
import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
        String content,
        Boolean isAnonymous,
        String guestPassword
) {

    public Comment toEntity(User user, Post post) {
        boolean isAnonymous = false;
        if (this.isAnonymous != null && this.isAnonymous) {
            isAnonymous = true;
        }
        return new Comment(
                this.content,
                user,
                isAnonymous,
                null,
                null,
                post
        );
    }

    public Comment toGuestEntity(Post post, String encodedPassword) {
        return new Comment(
                this.content,
                null,
                false,
                "익명",
                encodedPassword,
                post
        );
    }
}

