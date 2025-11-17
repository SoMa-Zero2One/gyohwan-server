package com.gyohwan.gyohwan.admin.dto;

import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OutgoingUniv의 information 수정 응답 DTO
 */
@Getter
@AllArgsConstructor
public class UpdateUnivInfoResponse {
    
    private Long univId;
    private String nameEn;
    private String nameKo;
    private String information;

    public static UpdateUnivInfoResponse from(OutgoingUniv outgoingUniv) {
        return new UpdateUnivInfoResponse(
                outgoingUniv.getId(),
                outgoingUniv.getNameEn(),
                outgoingUniv.getNameKo(),
                outgoingUniv.getInformation()
        );
    }
}

