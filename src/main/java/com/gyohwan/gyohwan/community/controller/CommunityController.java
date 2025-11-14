package com.gyohwan.gyohwan.community.controller;

import com.gyohwan.gyohwan.community.dto.*;
import com.gyohwan.gyohwan.community.service.CommentService;
import com.gyohwan.gyohwan.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        PostDetailResponse response = postService.findPost(postId, userId);
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
        PostDetailResponse response = postService.createPost(request, userId);
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
        PostDetailResponse response = postService.updatePost(postId, request, userId);
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
        postService.deletePost(postId, userId, password);
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
        CommentDto response = commentService.createComment(postId, request, userId);
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
        commentService.deleteComment(commentId, userId, password);
        return ResponseEntity.noContent().build();
    }
}
