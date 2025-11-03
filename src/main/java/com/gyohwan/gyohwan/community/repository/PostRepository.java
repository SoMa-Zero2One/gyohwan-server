package com.gyohwan.gyohwan.community.repository;

import com.gyohwan.gyohwan.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCountryCode(String countryCode, Pageable pageable);

    Page<Post> findByOutgoingUnivId(Long outgoingUnivId, Pageable pageable);
}
