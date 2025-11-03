package com.gyohwan.gyohwan.community.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.community.domain.Post;
import com.gyohwan.gyohwan.community.dto.*;
import com.gyohwan.gyohwan.community.repository.PostLikeRepository;
import com.gyohwan.gyohwan.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PostListResponse findPosts(String countryCode, Long outgoingUnivId, int page, int limit, Long userId) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage;

        if (countryCode != null) {
            postPage = postRepository.findByCountryCode(countryCode, pageable);
        } else if (outgoingUnivId != null) {
            postPage = postRepository.findByOutgoingUnivId(outgoingUnivId, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }

        List<PostListDto> posts = postPage.getContent().stream()
                .map(post -> {
                    // 좋아요 개수와 좋아요 여부 계산
                    int likeCount = (int) postLikeRepository.countByPostId(post.getId());
                    boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, post.getId());

                    return new PostListDto(
                            post.getId(),
                            post.getTitle(),
                            getNickname(post),
                            post.getCreatedAt(),
                            post.getCountryCode(),
                            post.getOutgoingUnivId(),
                            post.getComments() != null ? post.getComments().size() : 0,
                            likeCount,
                            isLiked
                    );
                })
                .collect(Collectors.toList());

        PostListResponse.PaginationInfo pagination = new PostListResponse.PaginationInfo(
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                page,
                limit
        );

        return new PostListResponse(pagination, posts);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse findPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, postId);

        List<CommentDto> comments = post.getComments().stream()
                .map(CommentDto::from)
                .collect(Collectors.toList());

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                getNickname(post),
                post.isAnonymous(),
                post.getCreatedAt(),
                post.getPostLikes() != null ? post.getPostLikes().size() : 0,
                isLiked,
                comments
        );
    }

    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request, Long userId) {
        Post post;

        if (userId != null) {
            // 회원 게시글
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            post = request.toEntity(user);
        } else {
            // 비회원 게시글
            if (request.guestPassword() == null || request.guestPassword().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "비회원은 비밀번호를 입력해야 합니다.");
            }
            String encodedPassword = passwordEncoder.encode(request.guestPassword());
            post = request.toEntity(encodedPassword);
        }

        Post savedPost = postRepository.save(post);
        log.info("게시글 생성: postId={}, userId={}", savedPost.getId(), userId);

        return PostDetailResponse.from(savedPost);
    }

    @Transactional
    public PostDetailResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 권한 확인
        validatePostOwnership(post, userId, request.guestPassword());

        // 회원인 경우에만 isAnonymous 업데이트 가능
        Boolean isAnonymous = post.getUser() != null ? request.isAnonymous() : null;
        post.update(request.title(), request.content(), isAnonymous);

        log.info("게시글 수정: postId={}, userId={}", postId, userId);

        return PostDetailResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId, String password) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 권한 확인
        validatePostOwnership(post, userId, password);

        postRepository.delete(post);
        log.info("게시글 삭제: postId={}, userId={}", postId, userId);
    }

    private void validatePostOwnership(Post post, Long userId, String password) {
        if (post.getUser() != null) {
            // 회원 게시글
            if (userId == null || !post.getUser().getId().equals(userId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
            }
        } else {
            // 비회원 게시글
            if (password == null || !passwordEncoder.matches(password, post.getGuestPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }
    }

    private String getNickname(Post post) {
        if (post.getUser() == null) {
            return post.getGuestNickname();
        } else if (post.isAnonymous()) {
            return "익명";
        } else {
            return post.getUser().getNickname();
        }
    }
}

