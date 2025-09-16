package com.gyohwan.gyohwan.legacyInu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationRequest {

    private String email;
    private Double gpa;
    // 인천대는 어학 점수를 받지 않음 - language 필드 제거
    private List<ApplicationRegistrationDto> applications;

    @Getter
    @NoArgsConstructor
    public static class ApplicationRegistrationDto {
        private Long seasonId;
        private List<ChoiceRegistrationDto> choices;
    }

    @Getter
    @NoArgsConstructor
    public static class ChoiceRegistrationDto {
        private Long slotId;
        private Integer choice; // 1지망, 2지망, 3지망 (인천대는 3지망까지만)
    }

    // 유효성 검증 메서드 - 인천대용으로 수정
    public boolean isValid() {
        boolean basicValid = email != null && !email.trim().isEmpty() && email.contains("@") &&
                gpa != null &&
                applications != null && !applications.isEmpty();

        if (!basicValid) {
            return false;
        }

        // 인천대는 3지망까지만 허용
        for (ApplicationRegistrationDto app : applications) {
            if (app.getChoices() != null) {
                for (ChoiceRegistrationDto choice : app.getChoices()) {
                    if (choice.getChoice() != null && choice.getChoice() > 3) {
                        return false; // 3지망을 초과하면 유효하지 않음
                    }
                }
            }
        }

        return true;
    }
}
