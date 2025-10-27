package com.gyohwan.gyohwan.compare.dto;

import com.gyohwan.gyohwan.compare.domain.Application;
import com.gyohwan.gyohwan.compare.domain.Choice;
import com.gyohwan.gyohwan.compare.domain.Slot;

import java.util.List;
import java.util.stream.Collectors;

public record ApplicationResponse(
        Long applicationId,
        Long seasonId,
        String nickname,
        List<ChoiceResponse> choices
) {
    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getSeason().getId(),
                application.getNickname(),
                application.getChoices().stream()
                        .map(ChoiceResponse::from)
                        .collect(Collectors.toList())
        );
    }

    public record ChoiceResponse(
            Integer choice,
            SlotInfo slot
    ) {
        public static ChoiceResponse from(Choice choice) {
            return new ChoiceResponse(
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

