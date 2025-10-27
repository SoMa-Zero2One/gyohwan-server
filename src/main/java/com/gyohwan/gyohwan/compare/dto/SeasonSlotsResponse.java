package com.gyohwan.gyohwan.compare.dto;

import com.gyohwan.gyohwan.compare.domain.Season;
import com.gyohwan.gyohwan.compare.domain.Slot;

import java.util.List;
import java.util.stream.Collectors;

public record SeasonSlotsResponse(
        Long seasonId,
        String seasonName,
        boolean hasApplied,
        long applicantCount,
        List<SlotInfo> slots
) {
    public static SeasonSlotsResponse from(Season season, List<Slot> slots, boolean hasApplied, long applicantCount) {
        return new SeasonSlotsResponse(
                season.getId(),
                season.getName(),
                hasApplied,
                applicantCount,
                slots.stream()
                        .map(SlotInfo::from)
                        .collect(Collectors.toList())
        );
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

