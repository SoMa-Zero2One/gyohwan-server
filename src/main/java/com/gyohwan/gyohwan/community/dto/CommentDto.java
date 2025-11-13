package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long commentId,
        String content,
        LocalDateTime createdAt,
        AuthorDto author
) {

    public static CommentDto from(Comment comment, Long userId) {
        AuthorDto author = AuthorDto.from(comment.getUser(), comment.isAnonymous(), comment.getGuestNickname(), userId);

        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                author
        );
    }
}
