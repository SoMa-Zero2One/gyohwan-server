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

    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "비밀번호는 최소 12자 이상이어야 합니다."),
    EMAIL_CONFIRM_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 요청된 적 없는 이메일입니다."),
    EMAIL_CONFIRM_CODE_DIFFERENT(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),

    // Password Reset
    PASSWORD_RESET_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "비밀번호 재설정 인증 코드가 만료되었거나 존재하지 않습니다."),
    PASSWORD_RESET_CODE_INVALID(HttpStatus.BAD_REQUEST, "비밀번호 재설정 인증 코드가 일치하지 않습니다."),
    SOCIAL_LOGIN_PASSWORD_RESET_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "소셜 로그인 계정은 비밀번호 재설정이 불가능합니다."),


    // Kakao
    KAKAO_REDIRECT_URI_MISMATCH(HttpStatus.BAD_REQUEST, "리다이렉트 uri가 잘못되었습니다."),
    INVALID_OR_EXPIRED_KAKAO_AUTH_CODE(HttpStatus.BAD_REQUEST, "사용할 수 없는 카카오 인증 코드입니다. 카카오 인증 코드는 일회용이며, 인증 만료 시간은 10분입니다."),

    // Google
    INVALID_OR_EXPIRED_GOOGLE_AUTH_CODE(HttpStatus.BAD_REQUEST, "사용할 수 없는 구글 인증 코드입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    SCHOOL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "학교 인증이 완료되지 않았습니다."),
    UNIV_MISMATCH(HttpStatus.FORBIDDEN, "해당 시즌은 귀하의 학교에서 지원할 수 없습니다."),

    // School Email
    SCHOOL_EMAIL_DOMAIN_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 학교 이메일 도메인입니다."),
    SCHOOL_EMAIL_CONFIRM_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었거나 요청된 적 없는 이메일입니다."),
    SCHOOL_EMAIL_CONFIRM_CODE_DIFFERENT(HttpStatus.BAD_REQUEST, "인증 코드가 일치하지 않습니다."),
    SCHOOL_EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "이미 학교 인증이 완료된 계정입니다."),
    SCHOOL_EMAIL_CONFIRM_DATA_CORRUPTED(HttpStatus.BAD_REQUEST, "인증 데이터가 손상되었습니다. 다시 인증을 요청해주세요."),

    // Application
    SEASON_NOT_FOUND(HttpStatus.NOT_FOUND, "시즌을 찾을 수 없습니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "이미 해당 시즌에 지원하였습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "지원 정보를 찾을 수 없습니다."),
    SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "슬롯을 찾을 수 없습니다."),
    GPA_NOT_FOUND(HttpStatus.NOT_FOUND, "학점 정보를 찾을 수 없습니다."),
    LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "어학 정보를 찾을 수 없습니다."),

    UNAUTHORIZED_GPA(HttpStatus.FORBIDDEN, "본인의 학점 정보만 사용할 수 있습니다."),
    UNAUTHORIZED_LANGUAGE(HttpStatus.FORBIDDEN, "본인의 어학 정보만 사용할 수 있습니다."),
    UNAUTHORIZED_APPLICATION(HttpStatus.FORBIDDEN, "본인의 지원서만 수정할 수 있습니다."),

    INVALID_GPA_CRITERIA(HttpStatus.BAD_REQUEST, "유효하지 않은 학점 기준입니다. 4.0, 4.3, 4.5 중 하나여야 합니다."),
    INVALID_LANGUAGE_TEST_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 어학 시험 유형입니다."),
    CHOICES_REQUIRED(HttpStatus.BAD_REQUEST, "지원 선택 항목은 필수입니다."),
    SEASON_DATA_INCOMPLETE(HttpStatus.BAD_REQUEST, "시즌의 학교 정보가 누락되었습니다."),
    MODIFY_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "지원서 수정 가능 횟수를 초과했습니다."),
    SEASON_SCHOOL_MISMATCH(HttpStatus.FORBIDDEN, "해당 시즌은 귀하의 학교에서 지원할 수 없습니다."),

    // Community
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COUNTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "국가를 찾을 수 없습니다."),
    UNIVERSITY_NOT_FOUND(HttpStatus.NOT_FOUND, "대학을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_POST_ACCESS(HttpStatus.FORBIDDEN, "게시글을 수정/삭제할 권한이 없습니다."),
    UNAUTHORIZED_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "댓글을 삭제할 권한이 없습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),

    // Window
    UNIV_NOT_FOUND(HttpStatus.NOT_FOUND, "대학을 찾을 수 없습니다."),
    ALREADY_FAVORITED(HttpStatus.CONFLICT, "이미 즐겨찾기에 추가되었습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기를 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
