package com.bespring.domain.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewStatsResponse {
    private Long totalAlarms;
    private Long successAlarms;
    private Long missedAlarms;
    private Double successRate;
    private Integer consecutiveDays;
    private String averageWakeTime;
    private Double last30DaysSuccessRate;
    private Double monthlySuccessRate;
    private Integer monthlyPoints;
}