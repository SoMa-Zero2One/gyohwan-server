package com.gyohwan.gyohwan.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("Custom Exception: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(ex);

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상하지 못한 오류가 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
