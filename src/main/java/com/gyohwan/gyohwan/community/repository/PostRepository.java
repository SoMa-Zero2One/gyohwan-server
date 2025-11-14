package com.gyohwan.gyohwan.community.repository;

import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCountry(Country country, Pageable pageable);

    Page<Post> findByOutgoingUnivId(Long outgoingUnivId, Pageable pageable);

    Page<Post> findByOutgoingUnivIdIn(List<Long> outgoingUnivIds, Pageable pageable);
}
