package com.gyohwan.gyohwan.article.dto;

import com.gyohwan.gyohwan.article.domain.Article;

public record ArticleDetailResponse(
        Long id,
        String uuid,
        String title,
        String content,
        String coverImageUrl
) {
    public static ArticleDetailResponse from(Article article) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getUuid(),
                article.getTitle(),
                article.getContent(),
                article.getCoverImageUrl()
        );
    }
}