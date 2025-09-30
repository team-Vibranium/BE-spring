package com.bespring.domain.statistics.controller;

import com.bespring.domain.statistics.dto.response.OverviewStatsResponse;
import com.bespring.domain.statistics.service.StatisticsService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "통계", description = "사용자 통계 데이터 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(
            summary = "전체 통계 조회",
            description = "사용자의 전체 알람 통계를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "전체 통계 조회 성공",
                    content = @Content(schema = @Schema(implementation = OverviewStatsResponse.class))
            )
    })
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<OverviewStatsResponse>> getOverviewStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        OverviewStatsResponse stats = statisticsService.getOverviewStats(userId);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(
            summary = "기간별 통계 조회",
            description = "지정한 기간 동안의 알람 통계를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "기간별 통계 조회 성공"
            )
    })
    @GetMapping("/period")
    public ResponseEntity<ApiResponse<StatisticsService.PeriodStats>> getPeriodStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "시작 일시 (ISO DateTime, 예: 2025-09-29T00:00:00.000)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startDate,
            @Parameter(description = "종료 일시 (ISO DateTime, 예: 2025-09-29T23:59:59.999)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endDate) {

        Long userId = userPrincipal.getUserId();
        // 서비스는 LocalDate를 받으므로 날짜로 변환
        java.time.LocalDate start = startDate.toLocalDate();
        java.time.LocalDate end = endDate.toLocalDate();
        StatisticsService.PeriodStats stats = statisticsService.getPeriodStats(userId, start, end);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(
            summary = "캘린더 통계 조회",
            description = "지정한 월의 알람 통계를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "캘린더 통계 조회 성공"
            )
    })
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<StatisticsService.CalendarStats>> getCalendarStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "년도 (YYYY)")
            @RequestParam int year,
            @Parameter(description = "월 (1-12)")
            @RequestParam int month) {

        Long userId = userPrincipal.getUserId();
        StatisticsService.CalendarStats stats = statisticsService.getCalendarStats(userId, year, month);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
