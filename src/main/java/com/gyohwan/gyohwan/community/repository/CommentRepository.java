package com.gyohwan.gyohwan.community.repository;

import com.gyohwan.gyohwan.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
