package com.gyohwan.gyohwan.community.dto;

import com.gyohwan.gyohwan.community.domain.Post;

import java.time.LocalDateTime;

public record PostListDto(
        Long id,
        String title,
        String authorNickname,
        LocalDateTime createdAt,
        String countryCode,
        Long outgoingUnivId,
        int commentCount // 댓글 개수
) {

    public static PostListDto from(Post post) {
        String nickname;
        // PostResponseDto와 동일한 닉네임 로직
        // 비회원은 isAnonymous=false, guestNickname="익명" 으로 저장됨
        if (post.getUser() == null && "익명".equals(post.getGuestNickname())) {
            // 비회원 ("익명")
            nickname = "익명";
        } else if (post.getUser() != null && post.isAnonymous()) {
            // 회원 + 익명 선택
            nickname = "익명";
        } else {
            // 회원 + 실명
            nickname = post.getUser() != null ? post.getUser().getNickname() : "알 수 없음";
        }

        // 댓글 개수 계산
        int count = (post.getComments() == null) ? 0 : post.getComments().size();

        return new PostListDto(
                post.getId(),
                post.getTitle(),
                nickname,
                post.getCreatedAt(),
                post.getCountryCode(),
                post.getOutgoingUnivId(),
                count
        );
    }
}
