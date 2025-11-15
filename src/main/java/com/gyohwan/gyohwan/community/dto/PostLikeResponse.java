package com.gyohwan.gyohwan.community.dto;

public record PostLikeResponse(
        Long postId,
        boolean isLiked,
        int likeCount
) {
    public static PostLikeResponse of(Long postId, boolean isLiked, int likeCount) {
        return new PostLikeResponse(postId, isLiked, likeCount);
    }
}

