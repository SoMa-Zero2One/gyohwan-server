package com.gyohwan.gyohwan.common.dto;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.Gpa;

import java.util.List;

public record UserGpaResponse(
        Long userId,
        List<GpaInfo> gpas
) {
    public record GpaInfo(
            Long gpaId,
            double score,
            Gpa.Criteria criteria,
            String verifyStatus,
            String statusReason
    ) {
        public static GpaInfo from(Gpa gpa) {
            return new GpaInfo(
                    gpa.getId(),
                    gpa.getScore(),
                    gpa.getCriteria(),
                    gpa.getVerifyStatus().name(),
                    gpa.getStatusReason()
            );
        }
    }

    public static UserGpaResponse from(User user) {
        return new UserGpaResponse(
                user.getId(),
                user.getGpas().stream()
                        .map(GpaInfo::from)
                        .toList()
        );
    }
}
