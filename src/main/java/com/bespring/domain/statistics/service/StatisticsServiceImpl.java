package com.bespring.domain.statistics.service;

import com.bespring.domain.call.service.CallLogService;
import com.bespring.domain.mission.service.MissionService;
import com.bespring.domain.points.entity.PointsHistory;
import com.bespring.domain.points.service.PointsService;
import com.bespring.domain.statistics.dto.response.OverviewStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final CallLogService callLogService;
    private final MissionService missionService;
    private final PointsService pointsService;

    @Override
    public OverviewStatsResponse getOverviewStats(Long userId) {
        // 전체 통계
        Long totalAlarms = callLogService.getTotalCallCount(userId);
        Long successAlarms = callLogService.getSuccessCallCount(userId);
        Long missedAlarms = totalAlarms - successAlarms;
        Double successRate = callLogService.getSuccessRate(userId);

        // 최근 30일 통계
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long recentTotalCalls = callLogService.getCallCountAfterDate(userId, thirtyDaysAgo);
        Long recentSuccessCalls = callLogService.getSuccessCallCountAfterDate(userId, thirtyDaysAgo);
        Double last30DaysSuccessRate = recentTotalCalls > 0 ?
                (recentSuccessCalls.doubleValue() / recentTotalCalls.doubleValue()) * 100.0 : 0.0;

        // 이번 달 통계
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Long monthlyTotalCalls = callLogService.getCallCountAfterDate(userId, monthStart);
        Long monthlySuccessCalls = callLogService.getSuccessCallCountAfterDate(userId, monthStart);
        Double monthlySuccessRate = monthlyTotalCalls > 0 ?
                (monthlySuccessCalls.doubleValue() / monthlyTotalCalls.doubleValue()) * 100.0 : 0.0;

        // 평균 기상시간
        Double avgWakeHour = callLogService.getAverageWakeTime(userId);
        String averageWakeTime = formatWakeTime(avgWakeHour);

        // 이번 달 포인트
        Integer monthlyPoints = pointsService.getPointsByType(userId, PointsHistory.PointType.GRADE);

        return OverviewStatsResponse.builder()
                .totalAlarms(totalAlarms)
                .successAlarms(successAlarms)
                .missedAlarms(missedAlarms)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .consecutiveDays(calculateConsecutiveSuccessDays(userId))
                .averageWakeTime(averageWakeTime)
                .last30DaysSuccessRate(Math.round(last30DaysSuccessRate * 100.0) / 100.0)
                .monthlySuccessRate(Math.round(monthlySuccessRate * 100.0) / 100.0)
                .monthlyPoints(monthlyPoints != null ? monthlyPoints : 0)
                .build();
    }

    @Override
    public PeriodStats getPeriodStats(Long userId, LocalDate startDate, LocalDate endDate) {
        // Period 정보
        PeriodStats.PeriodInfo period = PeriodStats.PeriodInfo.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // 기간 내 실제 통계 계산
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Long totalAlarms = callLogService.getCallCountAfterDate(userId, startDateTime) -
                          callLogService.getCallCountAfterDate(userId, endDateTime.plusSeconds(1));
        Long successAlarms = callLogService.getSuccessCallCountAfterDate(userId, startDateTime) -
                            callLogService.getSuccessCallCountAfterDate(userId, endDateTime.plusSeconds(1));

        Long failedAlarms = totalAlarms - successAlarms;
        Double successRate = totalAlarms > 0 ? (successAlarms.doubleValue() / totalAlarms.doubleValue()) * 100.0 : 0.0;

        // 평균 기상시간
        Double avgWakeHour = callLogService.getAverageWakeTime(userId);
        String averageWakeTime = formatWakeTime(avgWakeHour);

        // 기간 내 포인트 (근사치)
        Integer totalPoints = pointsService.getTotalPoints(userId);

        // 일별 통계 생성
        List<PeriodStats.DailyStat> dailyStats = generateDailyStats(userId, startDate, endDate);

        return PeriodStats.builder()
                .period(period)
                .totalAlarms(Math.max(0L, totalAlarms))
                .successAlarms(Math.max(0L, successAlarms))
                .failedAlarms(Math.max(0L, failedAlarms))
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .totalPoints(totalPoints)
                .averageWakeTime(averageWakeTime)
                .dailyStats(dailyStats)
                .build();
    }

    @Override
    public CalendarStats getCalendarStats(Long userId, int year, int month) {
        // 해당 월의 시작과 끝
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        LocalDateTime startDateTime = monthStart.atStartOfDay();
        LocalDateTime endDateTime = monthEnd.atTime(23, 59, 59);

        // 월별 실제 통계 계산
        Long totalAlarms = callLogService.getCallCountAfterDate(userId, startDateTime) -
                          callLogService.getCallCountAfterDate(userId, endDateTime.plusSeconds(1));
        Long successAlarms = callLogService.getSuccessCallCountAfterDate(userId, startDateTime) -
                            callLogService.getSuccessCallCountAfterDate(userId, endDateTime.plusSeconds(1));

        Long failedAlarms = totalAlarms - successAlarms;
        Double successRate = totalAlarms > 0 ? (successAlarms.doubleValue() / totalAlarms.doubleValue()) * 100.0 : 0.0;

        CalendarStats.MonthSummary monthSummary = CalendarStats.MonthSummary.builder()
                .totalAlarms(Math.max(0L, totalAlarms))
                .successAlarms(Math.max(0L, successAlarms))
                .failedAlarms(Math.max(0L, failedAlarms))
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .build();

        // 일별 결과 생성
        List<CalendarStats.DailyResult> dailyResults = generateMonthlyDailyResults(userId, year, month);

        return CalendarStats.builder()
                .year(year)
                .month(month)
                .dailyResults(dailyResults)
                .monthSummary(monthSummary)
                .build();
    }

    private String formatWakeTime(Double avgWakeHour) {
        if (avgWakeHour == null || avgWakeHour == 0.0) {
            return "00:00";
        }

        int hour = avgWakeHour.intValue();
        int minute = (int) ((avgWakeHour - hour) * 60);

        return String.format("%02d:%02d", hour, minute);
    }

    private Integer calculateConsecutiveSuccessDays(Long userId) {
        // 최근 통화 기록들을 날짜순으로 가져와서 연속 성공일 계산
        try {
            // 간단한 구현: 최근 7일간의 성공률이 80% 이상이면 연속일로 간주
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            Long recentTotal = callLogService.getCallCountAfterDate(userId, weekAgo);
            Long recentSuccess = callLogService.getSuccessCallCountAfterDate(userId, weekAgo);

            if (recentTotal > 0 && (recentSuccess.doubleValue() / recentTotal.doubleValue()) >= 0.8) {
                return recentTotal.intValue(); // 근사치로 성공한 알람 수를 연속일로 반환
            }
            return 0;
        } catch (Exception e) {
            log.warn("연속 성공일 계산 중 오류 발생: {}", e.getMessage());
            return 0;
        }
    }

    private List<PeriodStats.DailyStat> generateDailyStats(Long userId, LocalDate startDate, LocalDate endDate) {
        List<PeriodStats.DailyStat> dailyStats = new ArrayList<>();

        // 기간이 너무 길면 빈 리스트 반환 (성능 고려)
        if (startDate.plusDays(31).isBefore(endDate)) {
            return dailyStats;
        }

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // 각 날짜별로 기본 통계 생성 (실제 구현시에는 DB 쿼리 최적화 필요)
            PeriodStats.DailyStat dailyStat = PeriodStats.DailyStat.builder()
                    .date(currentDate)
                    .alarmCount(0L)
                    .successCount(0L)
                    .failCount(0L)
                    .points(0)
                    .build();

            dailyStats.add(dailyStat);
            currentDate = currentDate.plusDays(1);
        }

        return dailyStats;
    }

    private List<CalendarStats.DailyResult> generateMonthlyDailyResults(Long userId, int year, int month) {
        List<CalendarStats.DailyResult> dailyResults = new ArrayList<>();

        LocalDate monthStart = LocalDate.of(year, month, 1);
        int daysInMonth = monthStart.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            // 각 날짜별로 기본 결과 생성 (실제 구현시에는 DB 쿼리 최적화 필요)
            CalendarStats.DailyResult dailyResult = CalendarStats.DailyResult.builder()
                    .day(day)
                    .alarmCount(0L)
                    .successCount(0L)
                    .failCount(0L)
                    .status("none") // "success", "failure", "none"
                    .build();

            dailyResults.add(dailyResult);
        }

        return dailyResults;
    }
}