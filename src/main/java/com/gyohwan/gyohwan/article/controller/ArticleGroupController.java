package com.gyohwan.gyohwan.article.controller;

import com.gyohwan.gyohwan.article.domain.ArticleScopeType;
import com.gyohwan.gyohwan.article.dto.ArticleGroupDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupsResponse;
import com.gyohwan.gyohwan.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/article-groups")
@RequiredArgsConstructor
public class ArticleGroupController {

    private final ArticleService articleService;

    @GetMapping("")
    public ResponseEntity<ArticleGroupsResponse> getArticleGroups() {
        ArticleGroupsResponse response = articleService.findAllArticleGroups();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/common")
    public ResponseEntity<ArticleGroupDetailResponse> getCommonArticles() {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.COMMON, "COMMON");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<ArticleGroupDetailResponse> getCountryArticles(@PathVariable String countryCode) {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.COUNTRY, countryCode.toUpperCase());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/domestic-univ/{univCode}")
    public ResponseEntity<ArticleGroupDetailResponse> getDomesticUnivArticles(@PathVariable String univCode) {
        ArticleGroupDetailResponse response = articleService.findArticlesByGroup(ArticleScopeType.DOMESTIC_UNIV, univCode.toUpperCase());
        return ResponseEntity.ok(response);
    }
}
