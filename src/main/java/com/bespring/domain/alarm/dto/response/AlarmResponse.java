package com.bespring.domain.alarm.dto.response;

import com.bespring.domain.alarm.entity.UserAlarm;
import com.bespring.domain.alarm.entity.VoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "알람 정보 응답")
public class AlarmResponse {

    @Schema(description = "알람 ID", example = "123")
    private Long alarmId;

    @Schema(description = "알람 시간", example = "2024-01-15T07:30:00")
    private LocalDateTime alarmTime;

    @Schema(description = "AI 지시사항", example = "부드럽게 깨워주세요")
    private String instructions;

    @Schema(description = "음성 선택", example = "ALLOY")
    private VoiceType voice;

    @Schema(description = "음성 설명", example = "균형 잡힌 중성적 목소리")
    private String voiceDescription;

    @Schema(description = "활성화 상태", example = "true")
    private Boolean active;

    @Schema(description = "생성 시간", example = "2024-01-15T06:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2024-01-15T06:00:00")
    private LocalDateTime updatedAt;

    // 팩토리 메서드
    public static AlarmResponse from(UserAlarm userAlarm) {
        return new AlarmResponse(
            userAlarm.getId(),
            userAlarm.getAlarmTime(),
            userAlarm.getInstructions(),
            userAlarm.getVoice(),
            userAlarm.getVoice().getDescription(),
            userAlarm.getActive(),
            userAlarm.getCreatedAt(),
            userAlarm.getUpdatedAt()
        );
    }
}