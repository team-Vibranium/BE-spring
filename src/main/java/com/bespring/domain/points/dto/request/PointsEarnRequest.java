package com.bespring.domain.points.dto.request;

import com.bespring.domain.points.entity.PointsHistory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsEarnRequest {

    @NotNull(message = "포인트 타입은 필수입니다")
    private PointsHistory.PointType type;

    @NotNull(message = "포인트 금액은 필수입니다")
    @Positive(message = "포인트 금액은 양수여야 합니다")
    private Integer amount;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

    private String relatedAlarmId;
}