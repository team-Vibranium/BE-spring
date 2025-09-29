package com.bespring.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_002", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_004", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_005", "리소스를 찾을 수 없습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 사용자입니다."),

    // 알람 관련 에러
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALARM_001", "알람을 찾을 수 없습니다."),
    DUPLICATE_ALARM_TIME(HttpStatus.CONFLICT, "ALARM_002", "동일한 시간에 이미 알람이 설정되어 있습니다."),
    ALARM_TIME_PAST(HttpStatus.BAD_REQUEST, "ALARM_003", "과거 시간으로 알람을 설정할 수 없습니다."),
    MAX_ALARMS_EXCEEDED(HttpStatus.BAD_REQUEST, "ALARM_004", "최대 알람 개수를 초과했습니다."),
    ALARM_ALREADY_INACTIVE(HttpStatus.BAD_REQUEST, "ALARM_005", "이미 비활성화된 알람입니다."),

    // 통화 관련 에러
    CALL_NOT_FOUND(HttpStatus.NOT_FOUND, "CALL_001", "통화 기록을 찾을 수 없습니다."),
    CALL_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "CALL_002", "이미 진행 중인 통화가 있습니다."),
    CALL_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "CALL_003", "이미 종료된 통화입니다."),

    // OpenAI 관련 에러
    OPENAI_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "OPENAI_001", "OpenAI API 호출에 실패했습니다."),
    OPENAI_SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "OPENAI_002", "OpenAI 세션이 만료되었습니다."),
    OPENAI_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "OPENAI_003", "OpenAI API 사용량이 초과되었습니다."),

    // 포인트 관련 에러
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST, "POINT_001", "포인트가 부족합니다."),

    // 검증 관련 에러
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_001", "입력값 검증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}