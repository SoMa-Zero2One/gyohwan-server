package com.gyohwan.gyohwan.compare.dto;

import com.gyohwan.gyohwan.compare.domain.*;

import java.util.List;
import java.util.stream.Collectors;

public record ApplicationDetailResponse(
        Long applicationId,
        Long seasonId,
        String nickname,
        GpaInfo gpa,
        LanguageInfo language,
        List<ChoiceDetailResponse> choices
) {
    public static ApplicationDetailResponse from(Application application) {
        // Choice들에서 gpa와 language 정보 추출 (첫 번째 choice 기준)
        GpaInfo gpaInfo = null;
        LanguageInfo languageInfo = null;

        if (!application.getChoices().isEmpty()) {
            Choice firstChoice = application.getChoices().get(0);
            if (firstChoice.getGpaScore() != null) {
                gpaInfo = new GpaInfo(firstChoice.getGpaScore(), firstChoice.getGpaCriteria());
            }
            if (firstChoice.getLanguageTest() != null) {
                languageInfo = new LanguageInfo(
                        firstChoice.getLanguageTest(),
                        firstChoice.getLanguageScore(),
                        firstChoice.getLanguageGrade()
                );
            }
        }

        return new ApplicationDetailResponse(
                application.getId(),
                application.getSeason().getId(),
                application.getNickname(),
                gpaInfo != null ? gpaInfo : new GpaInfo(null, null),
                languageInfo != null ? languageInfo : new LanguageInfo(null, null, null),
                application.getChoices().stream()
                        .map(ChoiceDetailResponse::from)
                        .collect(Collectors.toList())
        );
    }

    public record GpaInfo(
            Double score,
            Gpa.Criteria criteria
    ) {
    }

    public record LanguageInfo(
            Language.TestType testType,
            String score,
            String grade
    ) {
    }

    public record ChoiceDetailResponse(
            Integer choice,
            SlotInfo slot
    ) {
        public static ChoiceDetailResponse from(Choice choice) {
            return new ChoiceDetailResponse(
                    choice.getChoice(),
                    SlotInfo.from(choice.getSlot())
            );
        }
    }

    public record SlotInfo(
            Long slotId,
            String name,
            String country,
            String logoUrl,
            Long choiceCount,
            String slotCount,
            String duration
    ) {
        public static SlotInfo from(Slot slot) {
            return new SlotInfo(
                    slot.getId(),
                    slot.getName(),
                    slot.getOutgoingUniv().getCountry(),
                    slot.getOutgoingUniv().getLogoUrl(),
                    (long) slot.getChoices().size(),
                    slot.getSlotCount(),
                    slot.getDuration() != null
                            ? (slot.getDuration() == Slot.Duration.SEMESTER ? "1학기" : "1년")
                            : "미정"
            );
        }
    }
}

