package com.gyohwan.gyohwan.article.service;

import com.gyohwan.gyohwan.article.domain.Article;
import com.gyohwan.gyohwan.article.domain.ArticleGroup;
import com.gyohwan.gyohwan.article.domain.ArticleScopeType;
import com.gyohwan.gyohwan.article.dto.ArticleDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupsResponse;
import com.gyohwan.gyohwan.article.dto.ArticleSummary;
import com.gyohwan.gyohwan.article.repository.ArticleGroupRepository;
import com.gyohwan.gyohwan.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleGroupRepository articleGroupRepository;
    private final ArticleRepository articleRepository;

    public ArticleGroupsResponse findAllArticleGroups() {
        var userStatus = new ArticleGroupsResponse.UserStatusDto(null, null);

        ArticleGroupsResponse.GroupInfoDto commonGroup = null;
        List<ArticleGroupsResponse.GroupInfoDto> countries = new ArrayList<>();
        List<ArticleGroupsResponse.GroupInfoDto> domesticUnivs = new ArrayList<>();

        List<ArticleGroup> allGroups = articleGroupRepository.findAll();

        for (ArticleGroup group : allGroups) {
            switch (group.getScopeType()) {
                case COMMON -> commonGroup = new ArticleGroupsResponse.GroupInfoDto(
                        group.getName(),
                        null,
                        "/article-groups/common"
                );
                case COUNTRY -> countries.add(new ArticleGroupsResponse.GroupInfoDto(
                        group.getName(),
                        group.getTargetCode(),
                        "/article-groups/country/" + group.getTargetCode()
                ));
                case DOMESTIC_UNIV -> domesticUnivs.add(new ArticleGroupsResponse.GroupInfoDto(
                        group.getName(),
                        group.getTargetCode(),
                        "/article-groups/domestic-univ/" + group.getTargetCode()
                ));
            }
        }

        return new ArticleGroupsResponse(userStatus, commonGroup, countries, domesticUnivs);
    }

    public ArticleGroupDetailResponse findArticlesByGroup(ArticleScopeType scopeType, String targetCode) {
        ArticleGroup group = articleGroupRepository.findByScopeTypeAndTargetCode(scopeType, targetCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청한 아티클 그룹을 찾을 수 없습니다."));

        List<ArticleSummary> articles = group.getArticleConnects().stream()
                .map(connection -> {
                    Article article = connection.getArticle();
                    return new ArticleSummary(
                            article.getUuid(),
                            article.getTitle(),
                            article.getCoverImageUrl()
                    );
                })
                .collect(Collectors.toList());

        return new ArticleGroupDetailResponse(group.getName(), articles);
    }

    public ArticleDetailResponse findArticleByUuid(String articleUuid) {
        Article article = articleRepository.findByUuid(articleUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아티클을 찾을 수 없습니다: " + articleUuid));
        return ArticleDetailResponse.from(article);
    }
}
