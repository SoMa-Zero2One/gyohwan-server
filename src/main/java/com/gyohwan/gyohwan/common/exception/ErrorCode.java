package com.gyohwan.gyohwan.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // SignIn, SignUp
    EMAIL_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 로그인에 실패하였습니다. 이메일 또는 비밀번호를 확인해주세요."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 최소 8자 이상이어야 합니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션을 찾을 수 없습니다."),
    NOT_SESSION_MEMBER(HttpStatus.FORBIDDEN, "해당 세션의 멤버가 아닙니다."),

    SESSION_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "세션 참가 요청을 찾을 수 없습니다."),
    ALREADY_REQUESTED_TO_JOIN_SESSION(HttpStatus.BAD_REQUEST, "이미 세션 참가 요청을 보냈습니다."),
    ONLY_OWNER_CAN_APPROVE_JOIN_REQUEST(HttpStatus.FORBIDDEN, "세션 참가 요청을 승인할 수 있는 권한이 없습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;
}
