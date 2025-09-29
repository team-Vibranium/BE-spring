package com.bespring.domain.call.dto.request;

import com.bespring.domain.call.entity.CallLog;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "통화 종료 요청")
public class CallEndRequest {

    @Schema(description = "통화 종료 시간", example = "2024-01-15T07:35:00", required = true)
    @NotNull(message = "통화 종료 시간은 필수입니다")
    private LocalDateTime callEnd;

    @Schema(description = "통화 결과", example = "SUCCESS", allowableValues = {"SUCCESS", "FAIL_NO_TALK", "FAIL_SNOOZE"}, required = true)
    @NotNull(message = "통화 결과는 필수입니다")
    private CallLog.CallResult result;

    @Schema(description = "스누즈 횟수", example = "0", defaultValue = "0")
    private Integer snoozeCount = 0;
}