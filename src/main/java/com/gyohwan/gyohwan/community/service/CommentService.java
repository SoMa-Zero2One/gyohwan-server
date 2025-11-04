package com.gyohwan.gyohwan.community.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.community.domain.Comment;
import com.gyohwan.gyohwan.community.domain.Post;
import com.gyohwan.gyohwan.community.dto.CommentCreateRequest;
import com.gyohwan.gyohwan.community.dto.CommentDto;
import com.gyohwan.gyohwan.community.repository.CommentRepository;
import com.gyohwan.gyohwan.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CommentDto createComment(Long postId, CommentCreateRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment;
        
        if (userId != null) {
            // 회원 댓글
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            comment = request.toEntity(user, post);
        } else {
            // 비회원 댓글
            if (request.guestPassword() == null || request.guestPassword().isBlank()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "비회원은 비밀번호를 입력해야 합니다.");
            }
            String encodedPassword = passwordEncoder.encode(request.guestPassword());
            comment = request.toGuestEntity(post, encodedPassword);
        }

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created: commentId={}, postId={}, userId={}", savedComment.getId(), postId, userId);

        return CommentDto.from(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId, String password) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 권한 확인
        validateCommentOwnership(comment, userId, password);

        commentRepository.delete(comment);
        log.info("Comment deleted: commentId={}, userId={}", commentId, userId);
    }

    private void validateCommentOwnership(Comment comment, Long userId, String password) {
        if (comment.getUser() != null) {
            // 회원 댓글
            if (userId == null || !comment.getUser().getId().equals(userId)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
            }
        } else {
            // 비회원 댓글
            if (password == null || !passwordEncoder.matches(password, comment.getGuestPassword())) {
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }
    }
}

