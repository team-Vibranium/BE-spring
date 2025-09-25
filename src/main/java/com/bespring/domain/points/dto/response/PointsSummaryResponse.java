package com.bespring.domain.points.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsSummaryResponse {

    private Integer consumptionPoints;
    private Integer gradePoints;
    private Integer totalPoints;
    private String currentGrade;

    public static PointsSummaryResponse of(Integer consumptionPoints, Integer gradePoints, String grade) {
        return PointsSummaryResponse.builder()
                .consumptionPoints(consumptionPoints != null ? consumptionPoints : 0)
                .gradePoints(gradePoints != null ? gradePoints : 0)
                .totalPoints((consumptionPoints != null ? consumptionPoints : 0) + (gradePoints != null ? gradePoints : 0))
                .currentGrade(grade)
                .build();
    }
}