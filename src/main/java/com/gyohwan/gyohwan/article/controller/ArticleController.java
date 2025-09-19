package com.gyohwan.gyohwan.article.controller;

import com.gyohwan.gyohwan.article.dto.ArticleDetailResponse;
import com.gyohwan.gyohwan.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/{articleUuid}")
    public ResponseEntity<ArticleDetailResponse> getArticleDetails(@PathVariable String articleUuid) {
        ArticleDetailResponse response = articleService.findArticleByUuid(articleUuid);
        return ResponseEntity.ok(response);
    }
}
