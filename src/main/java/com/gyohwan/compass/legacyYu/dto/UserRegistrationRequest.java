package com.gyohwan.compass.legacyYu.dto;

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
    private String language;
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
        private Integer choice; // 1지망, 2지망, 3지망 등
    }
    
    // 유효성 검증 메서드
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() && email.contains("@") &&
               gpa != null && 
               language != null && !language.trim().isEmpty() &&
               applications != null && !applications.isEmpty();
    }
}
