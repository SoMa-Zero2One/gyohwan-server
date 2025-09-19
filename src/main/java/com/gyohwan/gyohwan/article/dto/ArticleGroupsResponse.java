package com.gyohwan.gyohwan.article.dto;

import java.util.List;

public record ArticleGroupsResponse(
        UserStatusDto userStatus,
        GroupInfoDto basic,
        List<GroupInfoDto> countries,
        List<GroupInfoDto> domesticUnivs
) {
    public record UserStatusDto(
            String country,
            String domesticUniv
    ) {
    }

    public record GroupInfoDto(
            String name,
            String code, // 'default' 그룹은 code가 없을 수 있으므로 nullable
            String path
    ) {
    }
}