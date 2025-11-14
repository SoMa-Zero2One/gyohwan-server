package com.gyohwan.gyohwan.community.service;

import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.CountryRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.community.domain.Post;
import com.gyohwan.gyohwan.community.dto.*;
import com.gyohwan.gyohwan.community.repository.PostLikeRepository;
import com.gyohwan.gyohwan.community.repository.PostRepository;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.compare.repository.OutgoingUnivRepository;
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
    private final CountryRepository countryRepository;
    private final OutgoingUnivRepository outgoingUnivRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PostListResponse findPosts(String countryCode, Long outgoingUnivId, int page, int limit, Long userId) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage;

        if (countryCode != null) {
            Country country = countryRepository.findByCode(countryCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.COUNTRY_NOT_FOUND));
            postPage = postRepository.findByCountry(country, pageable);
        } else if (outgoingUnivId != null) {
            postPage = postRepository.findByOutgoingUnivId(outgoingUnivId, pageable);
        } else {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "countryCode 또는 outgoingUnivId를 입력해주세요.");
        }

        List<PostListDto> posts = postPage.getContent().stream()
                .map(post -> {
                    // 좋아요 개수와 좋아요 여부 계산
                    int likeCount = (int) postLikeRepository.countByPostId(post.getId());
                    boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, post.getId());

                    return PostListDto.from(post, likeCount, isLiked, userId);
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
    public PostListResponse findUnivPostsByCountry(String countryCode, int page, int limit, Long userId) {
        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNTRY_NOT_FOUND));

        // 해당 국가에 속한 모든 대학 조회
        List<OutgoingUniv> universities = outgoingUnivRepository.findByCountry(country);

        // 대학 ID 리스트 추출
        List<Long> univIds = universities.stream()
                .map(OutgoingUniv::getId)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 해당 대학들의 게시글 조회
        Page<Post> postPage;
        if (univIds.isEmpty()) {
            // 해당 국가에 대학이 없으면 빈 페이지 반환
            postPage = Page.empty(pageable);
        } else {
            postPage = postRepository.findByOutgoingUnivIdIn(univIds, pageable);
        }

        List<PostListDto> posts = postPage.getContent().stream()
                .map(post -> {
                    // 좋아요 개수와 좋아요 여부 계산
                    int likeCount = (int) postLikeRepository.countByPostId(post.getId());
                    boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, post.getId());

                    return PostListDto.from(post, likeCount, isLiked, userId);
                })
                .collect(Collectors.toList());

        PostListResponse.PaginationInfo pagination = new PostListResponse.PaginationInfo(
                postPage.getTotalElements(),
                postPage.getTotalPages(),
                page,
                limit
        );

        log.info("University posts retrieved by country: countryCode={}, univCount={}, totalPosts={}",
                countryCode, univIds.size(), postPage.getTotalElements());
        return new PostListResponse(pagination, posts);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse findPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, postId);

        return PostDetailResponse.from(post, isLiked, userId);
    }

    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request, Long userId) {
        Post post;

        validateOutgoingUnivAndCountry(request);

        Country country = null;
        OutgoingUniv outgoingUniv = null;
        if (request.countryCode() != null) {
            country = countryRepository.findByCode(request.countryCode())
                    .orElseThrow(() -> new CustomException(ErrorCode.COUNTRY_NOT_FOUND));
        } else {
            outgoingUniv = outgoingUnivRepository.findById(request.outgoingUnivId())
                    .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_NOT_FOUND));
        }

        if (userId != null) {
            // 회원 게시글
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            post = request.toEntity(user, country, outgoingUniv);
        } else {
            // 비회원 게시글
            if (request.guestPassword() == null || request.guestPassword().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "비회원은 비밀번호를 입력해야 합니다.");
            }
            String encodedPassword = passwordEncoder.encode(request.guestPassword());
            post = request.toEntity(encodedPassword, country, outgoingUniv);
        }

        Post savedPost = postRepository.save(post);
        log.info("Post created: postId={}, userId={}", savedPost.getId(), userId);

        return PostDetailResponse.from(savedPost, false, userId);
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

        log.info("Post updated: postId={}, userId={}", postId, userId);

        boolean isLiked = userId != null && postLikeRepository.existsByUserIdAndPostId(userId, postId);
        return PostDetailResponse.from(post, isLiked, userId);
    }

    @Transactional
    public void deletePost(Long postId, Long userId, String password) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 권한 확인
        validatePostOwnership(post, userId, password);

        postRepository.delete(post);
        log.info("Post deleted: postId={}, userId={}", postId, userId);
    }

    private void validateOutgoingUnivAndCountry(PostCreateRequest request) {
        if (request.countryCode() == null && request.outgoingUnivId() == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "countryCode 또는 outgoingUnivId를 입력해주세요.");
        }
        if (request.countryCode() != null && request.outgoingUnivId() != null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "countryCode와 outgoingUnivId는 동시에 입력할 수 없습니다.");
        }
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

}

