package com.bespring.domain.call.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "통화 시작 요청")
public class CallStartRequest {

    @Schema(description = "OpenAI 세션 ID", example = "sess_abc12345", required = true)
    @NotBlank(message = "세션 ID는 필수입니다")
    private String sessionId;
}