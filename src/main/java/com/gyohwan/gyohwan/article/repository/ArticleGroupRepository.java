package com.gyohwan.gyohwan.article.repository;

import com.gyohwan.gyohwan.article.domain.ArticleGroup;
import com.gyohwan.gyohwan.article.domain.ArticleScopeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleGroupRepository extends JpaRepository<ArticleGroup, Long> {
    Optional<ArticleGroup> findByScopeTypeAndTargetCode(ArticleScopeType scopeType, String targetCode);
}
