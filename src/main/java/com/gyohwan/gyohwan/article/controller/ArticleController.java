package com.gyohwan.gyohwan.article.controller;

import com.gyohwan.gyohwan.article.domain.ArticleScopeType;
import com.gyohwan.gyohwan.article.dto.ArticleDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupsResponse;
import com.gyohwan.gyohwan.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/v1/article-groups")
    public ResponseEntity<ArticleGroupsResponse> getArticleGroups() {
        ArticleGroupsResponse response = articleService.findAllArticleGroups();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/article-groups/default")
    public ResponseEntity<ArticleGroupDetailResponse> getDefaultArticles() {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.COMMON, "default");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/article-groups/country/{countryCode}")
    public ResponseEntity<ArticleGroupDetailResponse> getCountryArticles(@PathVariable String countryCode) {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.COUNTRY, countryCode.toUpperCase());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/article-groups/domestic-univ/{univCode}")
    public ResponseEntity<ArticleGroupDetailResponse> getDomesticUnivArticles(@PathVariable String univCode) {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.DOMESTIC_UNIV, univCode.toUpperCase());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/articles/{articleUuid}")
    public ResponseEntity<ArticleDetailResponse> getArticleDetails(@PathVariable String articleUuid) {
        ArticleDetailResponse response = articleService.findArticleByUuid(articleUuid);
        return ResponseEntity.ok(response);
    }
}
