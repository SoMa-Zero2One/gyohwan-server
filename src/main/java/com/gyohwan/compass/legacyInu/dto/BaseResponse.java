package com.gyohwan.compass.legacyInu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse {
    private boolean success;
    private String message;
}
