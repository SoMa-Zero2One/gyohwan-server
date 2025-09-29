package com.gyohwan.gyohwan.common.exception;


public record ErrorResponse(
        String title,
        int status,
        String detail
) {

    public ErrorResponse(CustomException e) {
        this(e.getErrorCode().name(), e.getErrorCode().getHttpStatus().value(), e.getMessage());
    }
}
