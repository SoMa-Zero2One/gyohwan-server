package com.gyohwan.gyohwan.article.service;

import com.gyohwan.gyohwan.article.domain.Article;
import com.gyohwan.gyohwan.article.domain.ArticleGroup;
import com.gyohwan.gyohwan.article.domain.ArticleScopeType;
import com.gyohwan.gyohwan.article.dto.ArticleDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupDetailResponse;
import com.gyohwan.gyohwan.article.dto.ArticleGroupsResponse;
import com.gyohwan.gyohwan.article.repository.ArticleGroupRepository;
import com.gyohwan.gyohwan.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // TODO: Spring Security 등 인증 정보를 통해 실제 사용자 정보 조회
        // 예시: User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userStatus = new ArticleGroupsResponse.UserStatusDto("JP", "KHU");

        ArticleGroupsResponse.GroupInfoDto defaultGroup = null;
        List<ArticleGroupsResponse.GroupInfoDto> countries = new ArrayList<>();
        List<ArticleGroupsResponse.GroupInfoDto> domesticUnivs = new ArrayList<>();

        List<ArticleGroup> allGroups = articleGroupRepository.findAll();

        for (ArticleGroup group : allGroups) {
            switch (group.getScopeType()) {
                case COMMON -> defaultGroup = new ArticleGroupsResponse.GroupInfoDto(
                        group.getName(),
                        null,
                        "/article-groups/default"
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

        return new ArticleGroupsResponse(userStatus, defaultGroup, countries, domesticUnivs);
    }

    public ArticleGroupDetailResponse findArticlesByGroup(ArticleScopeType scopeType, String targetCode) {
        ArticleGroup group = articleGroupRepository.findByScopeTypeAndTargetCode(scopeType, targetCode)
                .orElseThrow(() -> new ResourceNotFoundException("요청한 아티클 그룹을 찾을 수 없습니다."));

        List<ArticleSummaryResponse> articles = group.getArticleConnects().stream()
                .map(connection -> {
                    Article article = connection.getArticle();
                    return new ArticleSummaryResponse(
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
                .orElseThrow(() -> new ResourceNotFoundException("아티클을 찾을 수 없습니다: " + articleUuid));
        return ArticleDetailResponse.from(article);
    }
}