package com.gyohwan.gyohwan.common.exception;


public record ErrorResponse(
        int status,
        String error,
        String message
) {

    public ErrorResponse(CustomException e) {
        this(e.getErrorCode().getHttpStatus().value(), e.getErrorCode().name(), e.getMessage());
    }
}
