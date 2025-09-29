package com.bespring.domain.realtime.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "OpenAI Realtime API 세션 응답")
public class SessionResponse {

    @Schema(description = "OpenAI ephemeral key", example = "eph_abc123def456ghi789", required = true)
    private String ephemeralKey;

    @Schema(description = "세션 ID", example = "sess_abc12345", required = true)
    private String sessionId;

    @Schema(description = "만료 시간 (초)", example = "900", required = true)
    private Long expiresInSeconds;
}