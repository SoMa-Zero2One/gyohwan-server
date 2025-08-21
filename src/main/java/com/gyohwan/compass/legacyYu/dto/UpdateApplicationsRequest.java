package com.gyohwan.compass.legacyYu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateApplicationsRequest {
    private List<ApplicationUpdateDto> applications;
    
    @Getter
    @NoArgsConstructor
    public static class ApplicationUpdateDto {
        private Long seasonId;
        private List<ChoiceUpdateDto> choices;
    }
    
    @Getter
    @NoArgsConstructor
    public static class ChoiceUpdateDto {
        private Long slotId;
        private Integer choice; // 1지망, 2지망, 3지망 등
    }
}
