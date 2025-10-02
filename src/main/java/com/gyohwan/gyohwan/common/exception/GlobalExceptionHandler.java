package com.gyohwan.gyohwan.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ProblemDetail handleCustomException(CustomException ex) {
        log.error("Custom Exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getErrorCode().getHttpStatus(), ex.getMessage());

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    protected ProblemDetail handleException(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "예상하지 못한 오류가 발생했습니다.");
        return problemDetail;
    }
}
