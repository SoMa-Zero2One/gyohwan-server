package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        String authorNickname,
        boolean isAnonymous,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long postId
) {

    public static CommentDto from(Comment comment) {
        String nickname;
        if (comment.isAnonymous()) {
            nickname = "익명";
        } else {
            nickname = comment.getUser() != null ? comment.getUser().getNickname() : "익명"; // 탈퇴한 회원
        }

        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                nickname,
                comment.isAnonymous(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getPost().getId()
        );
    }
}
