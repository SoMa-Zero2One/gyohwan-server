package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Post;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        LocalDateTime createdAt,
        AuthorDto author,
        int likeCount,
        boolean isLiked,
        List<CommentDto> comments
) {

    public static PostDetailResponse from(Post post, boolean isLiked) {
        AuthorDto author = AuthorDto.from(post.getUser(), post.isAnonymous(), post.getGuestNickname());

        // 댓글 목록 DTO 변환
        List<CommentDto> commentDtos = post.getComments() != null ?
                post.getComments().stream()
                        .map(CommentDto::from)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                author,
                post.getPostLikes() != null ? post.getPostLikes().size() : 0,
                isLiked,
                commentDtos
        );
    }
}

