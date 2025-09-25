package com.bespring.domain.call.dto.request;

import com.bespring.domain.call.entity.CallLog;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallLogCreateRequest {

    @NotNull(message = "통화 시작 시간은 필수입니다")
    private LocalDateTime callStart;

    private LocalDateTime callEnd;

    @NotNull(message = "통화 결과는 필수입니다")
    private CallLog.CallResult result;

    @NotNull(message = "스누즈 횟수는 필수입니다")
    @Min(value = 0, message = "스누즈 횟수는 0 이상이어야 합니다")
    @Max(value = 3, message = "스누즈 횟수는 최대 3회까지 가능합니다")
    private Integer snoozeCount = 0;
}