package com.gyohwan.gyohwan.common.dto;

import com.gyohwan.gyohwan.compare.domain.Gpa;

public record GpaResponse(
        Long gpaId,
        Double score,
        Gpa.Criteria criteria,
        String verifyStatus,
        String statusReason
) {
    public static GpaResponse from(Gpa gpa) {
        return new GpaResponse(
                gpa.getId(),
                gpa.getScore(),
                gpa.getCriteria(),
                gpa.getVerifyStatus().name(),
                gpa.getStatusReason()
        );
    }
}
