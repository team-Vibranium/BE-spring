package com.bespring.global.dto;

import com.bespring.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    private boolean success = false;

    @Schema(description = "에러 코드", example = "ALARM_001")
    private String code;

    @Schema(description = "에러 메시지", example = "알람을 찾을 수 없습니다.")
    private String message;

    @Schema(description = "상세 정보", example = "알람 ID: 123")
    private String detail;

    @Schema(description = "발생 시간", example = "2024-01-15T07:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "검증 에러 목록")
    private List<ValidationError> validationErrors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "검증 에러 상세")
    public static class ValidationError {
        @Schema(description = "필드명", example = "alarmTime")
        private String field;

        @Schema(description = "에러 메시지", example = "알람 시간은 필수입니다")
        private String message;

        @Schema(description = "거부된 값", example = "null")
        private Object rejectedValue;
    }

    // 팩토리 메서드들
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            false,
            errorCode.getCode(),
            errorCode.getMessage(),
            null,
            LocalDateTime.now(),
            null
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String detail) {
        return new ErrorResponse(
            false,
            errorCode.getCode(),
            errorCode.getMessage(),
            detail,
            LocalDateTime.now(),
            null
        );
    }

    public static ErrorResponse withValidationErrors(String code, String message, List<ValidationError> validationErrors) {
        return new ErrorResponse(
            false,
            code,
            message,
            null,
            LocalDateTime.now(),
            validationErrors
        );
    }
}