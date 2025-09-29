package com.bespring.domain.call.dto.response;

import com.bespring.domain.call.dto.Utterance;
import com.bespring.domain.call.entity.CallLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "통화 상세 정보 응답")
public class CallDetailResponse {

    @Schema(description = "통화 ID", example = "123")
    private Long callId;

    @Schema(description = "통화 시작 시간", example = "2024-01-15T07:30:00")
    private LocalDateTime callStart;

    @Schema(description = "통화 종료 시간", example = "2024-01-15T07:35:00")
    private LocalDateTime callEnd;

    @Schema(description = "통화 결과", example = "SUCCESS")
    private CallLog.CallResult result;

    @Schema(description = "스누즈 횟수", example = "0")
    private Integer snoozeCount;

    @Schema(description = "대화 내용 리스트")
    private List<Utterance> conversation;

    @Schema(description = "생성 시간", example = "2024-01-15T07:30:00")
    private LocalDateTime createdAt;
}