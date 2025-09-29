package com.bespring.domain.call.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "통화 시작 응답")
public class CallStartResponse {

    @Schema(description = "통화 ID", example = "123")
    private Long callId;

    @Schema(description = "세션 ID", example = "sess_abc12345")
    private String sessionId;

    @Schema(description = "통화 시작 시간", example = "2024-01-15T07:30:00")
    private LocalDateTime callStart;
}