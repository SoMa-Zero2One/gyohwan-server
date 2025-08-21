package com.gyohwan.compass.legacyYu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse {
    private boolean status;
    private String detail;
}