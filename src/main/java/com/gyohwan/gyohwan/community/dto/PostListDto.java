package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Post;

import java.time.LocalDateTime;

public record PostListDto(
        Long postId,
        String title,
        String content,
        LocalDateTime createdAt,
        AuthorDto author,
        int likeCount,
        int commentsCount,
        boolean isLiked
) {

    public static PostListDto from(Post post, int likeCount, boolean isLiked) {
        AuthorDto author = AuthorDto.from(post.getUser(), post.isAnonymous(), post.getGuestNickname());
        int commentsCount = (post.getComments() == null) ? 0 : post.getComments().size();

        return new PostListDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                author,
                likeCount,
                commentsCount,
                isLiked
        );
    }
}
