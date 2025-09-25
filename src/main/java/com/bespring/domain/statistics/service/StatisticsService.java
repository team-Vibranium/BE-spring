package com.bespring.domain.statistics.service;

import com.bespring.domain.statistics.dto.response.OverviewStatsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    OverviewStatsResponse getOverviewStats(Long userId);

    PeriodStats getPeriodStats(Long userId, LocalDate startDate, LocalDate endDate);

    CalendarStats getCalendarStats(Long userId, int year, int month);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class PeriodStats {
        private PeriodInfo period;
        private Long totalAlarms;
        private Long successAlarms;
        private Long failedAlarms;
        private Double successRate;
        private Integer totalPoints;
        private String averageWakeTime;
        private List<DailyStat> dailyStats;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PeriodInfo {
            private LocalDate startDate;
            private LocalDate endDate;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DailyStat {
            private LocalDate date;
            private Long alarmCount;
            private Long successCount;
            private Long failCount;
            private Integer points;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CalendarStats {
        private Integer year;
        private Integer month;
        private List<DailyResult> dailyResults;
        private MonthSummary monthSummary;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DailyResult {
            private Integer day;
            private Long alarmCount;
            private Long successCount;
            private Long failCount;
            private String status; // "success", "failure", "none"
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MonthSummary {
            private Long totalAlarms;
            private Long successAlarms;
            private Long failedAlarms;
            private Double successRate;
        }
    }
}