package com.bespring.domain.points.controller;

import com.bespring.domain.points.dto.request.PointsEarnRequest;
import com.bespring.domain.points.dto.request.PointsSpendRequest;
import com.bespring.domain.points.dto.response.PointsSummaryResponse;
import com.bespring.domain.points.entity.PointsHistory;
import com.bespring.domain.points.service.PointsService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "포인트 시스템", description = "포인트 조회, 획득, 사용 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PointsController {

    private final PointsService pointsService;

    @Operation(
            summary = "포인트 현황 조회",
            description = "사용자의 현재 포인트 현황을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "포인트 현황 조회 성공",
                    content = @Content(schema = @Schema(implementation = PointsSummaryResponse.class))
            )
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<PointsSummaryResponse>> getPointsSummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        PointsSummaryResponse summary = pointsService.getPointsSummary(userId);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @Operation(
            summary = "포인트 내역 조회",
            description = "사용자의 포인트 사용 내역을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "포인트 내역 조회 성공"
            )
    })
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPointsHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "포인트 타입 필터 (GRADE, CONSUMPTION)")
            @RequestParam(required = false) PointsHistory.PointType type,
            @Parameter(description = "오프셋 (0부터 시작)")
            @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "페이지 크기 (1-100)")
            @RequestParam(defaultValue = "20") int limit) {

        Long userId = userPrincipal.getUserId();
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<PointsHistory> historyPage = pointsService.getPointsHistory(userId, type, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", historyPage.getContent());
        response.put("totalCount", historyPage.getTotalElements());
        response.put("hasMore", historyPage.hasNext());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "포인트 획득",
            description = "사용자가 포인트를 획득합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "포인트 획득 성공"
            )
    })
    @PostMapping("/earn")
    public ResponseEntity<ApiResponse<Map<String, Object>>> earnPoints(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PointsEarnRequest request) {

        Long userId = userPrincipal.getUserId();

        log.info("Earn points request: userId={}, amount={}, type={}",
                userId, request.getAmount(), request.getType());

        PointsHistory transaction = pointsService.earnPoints(userId, request);
        PointsSummaryResponse newBalance = pointsService.getPointsSummary(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("transaction", transaction);
        response.put("newBalance", newBalance);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "포인트 사용",
            description = "사용자가 포인트를 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "포인트 사용 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "422",
                    description = "포인트 부족"
            )
    })
    @PostMapping("/spend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> spendPoints(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PointsSpendRequest request) {

        Long userId = userPrincipal.getUserId();

        log.info("Spend points request: userId={}, amount={}, type={}",
                userId, request.getAmount(), request.getType());

        PointsHistory transaction = pointsService.spendPoints(userId, request);
        PointsSummaryResponse newBalance = pointsService.getPointsSummary(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("transaction", transaction);
        response.put("newBalance", newBalance);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}