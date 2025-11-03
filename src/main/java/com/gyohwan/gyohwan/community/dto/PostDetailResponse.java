package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Post;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public record PostDetailResponse(
        Long id,
        String title,
        String content,
        String nickname,
        boolean isAnonymous,
        LocalDateTime createdAt,
        int likeCount,
        boolean isLiked,
        List<CommentDto> comments
) {

    public static PostDetailResponse from(Post post) {
        String nickname;
        if (post.getUser() == null) {
            nickname = post.getGuestNickname();
        } else if (post.getUser() != null && post.isAnonymous()) {
            nickname = "익명";
        } else {
            nickname = post.getUser() != null ? post.getUser().getNickname() : "익명"; // 탈퇴한 회원
        }

        // 댓글 목록 DTO 변환 (N+1 문제 방지를 위해 fetch join 필요할 수 있음)
        List<CommentDto> commentDtos = post.getComments() != null ?
                post.getComments().stream()
                        .map(CommentDto::from)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                nickname,
                post.isAnonymous(),
                post.getCreatedAt(),
                post.getPostLikes() != null ? post.getPostLikes().size() : 0,
                false, // 이후 수정
                commentDtos
        );
    }
}

