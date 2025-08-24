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
    private List<Long> slotIds; // 최대 5개 슬롯 ID (지원할 교환대학 슬롯들)
    
    // 유효성 검증 메서드
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() && email.contains("@") &&
               gpa != null && 
               language != null && !language.trim().isEmpty() &&
               slotIds != null && !slotIds.isEmpty() && slotIds.size() <= 5;
    }
}
