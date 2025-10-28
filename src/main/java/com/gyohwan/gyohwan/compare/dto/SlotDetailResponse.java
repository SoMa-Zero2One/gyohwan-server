package com.gyohwan.gyohwan.compare.dto;

import com.gyohwan.gyohwan.compare.domain.Choice;
import com.gyohwan.gyohwan.compare.domain.Gpa;
import com.gyohwan.gyohwan.compare.domain.Slot;

import java.util.List;
import java.util.stream.Collectors;

public record SlotDetailResponse(
        Long slotId,
        Long seasonId,
        String name,
        String country,
        String logoUrl,
        String hompageUrl,
        Long choiceCount,
        String slotCount,
        String duration,
        String etc,
        boolean hasApplied,
        List<ChoiceInfo> choices
) {
    public static SlotDetailResponse from(Slot slot, boolean hasApplied) {
        return new SlotDetailResponse(
                slot.getId(),
                slot.getSeason().getId(),
                slot.getName(),
                slot.getOutgoingUniv().getCountry(),
                slot.getOutgoingUniv().getLogoUrl(),
                slot.getOutgoingUniv().getHomepageUrl(),
                (long) slot.getChoices().size(),
                slot.getSlotCount(),
                slot.getDuration() != null
                        ? (slot.getDuration() == Slot.Duration.SEMESTER ? "1학기" : "1년")
                        : "미정",
                slot.getEtc(),
                hasApplied,
                slot.getChoices().stream()
                        .map(choice -> ChoiceInfo.from(choice, hasApplied))
                        .collect(Collectors.toList())
        );
    }

    public record ChoiceInfo(
            Long applicationId,
            String nickname,
            Integer choice,
            Double gpaScore,
            Double gpaCriteria,
            String languageTest,
            String languageGrade,
            String languageScore,
            Double extraScore,
            Double score,
            String etc
    ) {
        public static ChoiceInfo from(Choice choice, boolean hasApplied) {
            if (hasApplied) {
                return new ChoiceInfo(
                        choice.getApplication().getId(),
                        choice.getApplication().getNickname(),
                        choice.getChoice(),
                        choice.getGpaScore(),
                        choice.getGpaCriteria() != null ? getCriteriaValue(choice.getGpaCriteria()) : null,
                        choice.getLanguageTest() != null ? choice.getLanguageTest().name() : null,
                        choice.getLanguageGrade(),
                        choice.getLanguageScore(),
                        choice.getApplication().getExtraScore(),
                        choice.getScore(),
                        ""
                );
            } else {
                // 미참여 또는 미인증 시 민감한 정보는 null로 반환
                return new ChoiceInfo(
                        choice.getApplication().getId(),
                        choice.getApplication().getNickname(),
                        choice.getChoice(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        ""
                );
            }
        }

        private static Double getCriteriaValue(Gpa.Criteria criteria) {
            return switch (criteria) {
                case _4_5 -> 4.5;
                case _4_3 -> 4.3;
                case _4_0 -> 4.0;
            };
        }
    }
}

