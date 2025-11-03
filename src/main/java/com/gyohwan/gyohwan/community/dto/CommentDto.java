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
        if (comment.getUser() == null) {
            // 비회원 댓글
            nickname = comment.getGuestNickname();
        } else if (comment.isAnonymous()) {
            // 회원 + 익명
            nickname = "익명";
        } else {
            // 회원 + 실명
            nickname = comment.getUser().getNickname();
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
