package com.gyohwan.gyohwan.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostListResponse {

    private PaginationInfo pagination;
    private List<PostListDto> posts;

    @Getter
    @AllArgsConstructor
    public static class PaginationInfo {
        private long totalItems;
        private int totalPages;
        private int currentPage;
        private int limit;
    }
}

