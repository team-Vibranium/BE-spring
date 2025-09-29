package com.bespring.domain.alarm.dto.request;

import com.bespring.domain.alarm.entity.VoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "알람 생성 요청")
public class AlarmCreateRequest {

    @Schema(description = "알람 시간", example = "2024-01-15T07:30:00", required = true)
    @NotNull(message = "알람 시간은 필수입니다")
    private LocalDateTime alarmTime;

    @Schema(description = "AI 지시사항", example = "부드럽게 깨워주세요", required = true)
    @NotBlank(message = "지시사항은 필수입니다")
    private String instructions;

    @Schema(description = "음성 선택", example = "ALLOY",
            allowableValues = {"ALLOY", "ASH", "BALLAD", "CORAL", "ECHO", "SAGE", "SHIMMER", "VERSE"},
            required = true)
    @NotNull(message = "음성 선택은 필수입니다")
    private VoiceType voice;
}