package com.gyohwan.gyohwan.article.dto;

import java.util.List;

public record ArticleGroupDetailResponse(
        String groupName,
        List<ArticleSummary> articles
) {
}
