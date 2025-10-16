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

    PASSWORD_CHANGE_FAILED(HttpStatus.BAD_REQUEST, "비밀번호 변경에 실패하였습니다. 현재 비밀번호를 확인해주세요."),

    EMAIL_CONFIRM_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 요청된 적 없는 이메일입니다."),
    EMAIL_CONFIRM_CODE_DIFFERENT(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),


    // Kakao
    KAKAO_REDIRECT_URI_MISMATCH(HttpStatus.BAD_REQUEST, "리다이렉트 uri가 잘못되었습니다."),
    INVALID_OR_EXPIRED_KAKAO_AUTH_CODE(HttpStatus.BAD_REQUEST, "사용할 수 없는 카카오 인증 코드입니다. 카카오 인증 코드는 일회용이며, 인증 만료 시간은 10분입니다."),

    // Google
    INVALID_OR_EXPIRED_GOOGLE_AUTH_CODE(HttpStatus.BAD_REQUEST, "사용할 수 없는 구글 인증 코드입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

    // School Email
    SCHOOL_EMAIL_DOMAIN_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 학교 이메일 도메인입니다."),
    SCHOOL_EMAIL_CONFIRM_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 요청된 적 없는 이메일입니다."),
    SCHOOL_EMAIL_CONFIRM_CODE_DIFFERENT(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    SCHOOL_EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "이미 학교 인증이 완료된 계정입니다."),

    // Application
    SEASON_NOT_FOUND(HttpStatus.NOT_FOUND, "시즌을 찾을 수 없습니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 해당 시즌에 지원하였습니다."),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "슬롯을 찾을 수 없습니다."),
    GPA_NOT_FOUND(HttpStatus.NOT_FOUND, "학점 정보를 찾을 수 없습니다."),
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "어학 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_GPA(HttpStatus.FORBIDDEN, "본인의 학점 정보만 사용할 수 있습니다."),
    UNAUTHORIZED_LANGUAGE(HttpStatus.FORBIDDEN, "본인의 어학 정보만 사용할 수 있습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
