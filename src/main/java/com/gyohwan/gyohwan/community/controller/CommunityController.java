package com.gyohwan.gyohwan.community.controller;

import com.gyohwan.gyohwan.community.dto.*;
import com.gyohwan.gyohwan.community.service.CommentService;
import com.gyohwan.gyohwan.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/community")
@RestController
public class CommunityController {

    private final PostService postService;
    private final CommentService commentService;

    /**
     * 게시글 목록 조회 (국가 코드 또는 대학 ID로 필터링)
     */
    @GetMapping("/posts")
    public ResponseEntity<PostListResponse> findPosts(
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) Long outgoingUnivId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] GET /posts - countryCode={}, outgoingUnivId={}, page={}, limit={}, userId={}",
                countryCode, outgoingUnivId, page, limit, userId);

        PostListResponse response = postService.findPosts(countryCode, outgoingUnivId, page, limit, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 국가별 게시글 목록 조회
     */
    @GetMapping("/posts/country/{countryCode}")
    public ResponseEntity<PostListResponse> findPostsByCountry(
            @PathVariable String countryCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] GET /posts/country/{} - page={}, limit={}, userId={}", countryCode, page, limit, userId);

        PostListResponse response = postService.findUnivPostsByCountry(countryCode, page, limit, userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> findPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] GET /posts/{} - userId={}", postId, userId);

        PostDetailResponse response = postService.findPost(postId, userId);

        log.info("[Community] GET /posts/{} - Response: title={}, likeCount={}, commentsCount={}",
                postId, response.title(), response.likeCount(), response.comments().size());
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 작성 (회원/비회원)
     */
    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createPost(
            @Valid @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] POST /posts - userId={}, title={}, isGuest={}",
                userId, request.title(), userId == null);

        PostDetailResponse response = postService.createPost(request, userId);

        log.info("[Community] POST /posts - Response: postId={}", response.postId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] PUT /posts/{} - userId={}, title={}", postId, userId, request.title());

        PostDetailResponse response = postService.updatePost(postId, request, userId);

        log.info("[Community] PUT /posts/{} - Updated successfully", postId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestBody(required = false) DeleteRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        String password = request != null ? request.password() : null;
        log.info("[Community] DELETE /posts/{} - userId={}", postId, userId);

        postService.deletePost(postId, userId, password);

        log.info("[Community] DELETE /posts/{} - Deleted successfully", postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 작성 (회원/비회원)
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        log.info("[Community] POST /posts/{}/comments - userId={}, isGuest={}", postId, userId, userId == null);

        CommentDto response = commentService.createComment(postId, request, userId);

        log.info("[Community] POST /posts/{}/comments - Response: commentId={}", postId, response.commentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestBody(required = false) DeleteRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;
        String password = request != null ? request.password() : null;
        log.info("[Community] DELETE /comments/{} - userId={}", commentId, userId);

        commentService.deleteComment(commentId, userId, password);

        log.info("[Community] DELETE /comments/{} - Deleted successfully", commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 게시글 좋아요 추가
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> addPostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            log.warn("[Community] POST /posts/{}/like - Unauthorized access attempt", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("[Community] POST /posts/{}/like - userId={}", postId, userId);

        PostLikeResponse response = postService.addPostLike(postId, userId);

        log.info("[Community] POST /posts/{}/like - Response: likeCount={}", postId, response.likeCount());
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 좋아요 취소
     */
    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> removePostLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            log.warn("[Community] DELETE /posts/{}/like - Unauthorized access attempt", postId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        log.info("[Community] DELETE /posts/{}/like - userId={}", postId, userId);

        PostLikeResponse response = postService.removePostLike(postId, userId);

        log.info("[Community] DELETE /posts/{}/like - Response: likeCount={}", postId, response.likeCount());
        return ResponseEntity.ok(response);
    }
}
