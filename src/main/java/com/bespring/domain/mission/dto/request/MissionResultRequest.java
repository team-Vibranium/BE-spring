package com.bespring.domain.mission.dto.request;

import com.bespring.domain.mission.entity.MissionResult;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionResultRequest {

    @NotNull(message = "통화 로그 ID는 필수입니다")
    private Long callLogId;

    @NotNull(message = "미션 타입은 필수입니다")
    private MissionResult.MissionType missionType;

    @NotNull(message = "성공 여부는 필수입니다")
    private Boolean success;
}