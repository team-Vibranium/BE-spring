package com.bespring.domain.call.controller;

import com.bespring.domain.call.dto.request.CallLogCreateRequest;
import com.bespring.domain.call.entity.CallLog;
import com.bespring.domain.call.service.CallLogService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "통화 기록", description = "알람 통화 기록 생성 및 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/call-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CallLogController {

    private final CallLogService callLogService;

    @Operation(
            summary = "통화 기록 생성",
            description = "알람 통화 결과를 기록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "통화 기록 생성 성공",
                    content = @Content(schema = @Schema(implementation = CallLog.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CallLog>> createCallLog(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CallLogCreateRequest request) {

        Long userId = userPrincipal.getUserId();

        log.info("Create call log request: userId={}, result={}, snoozeCount={}",
                userId, request.getResult(), request.getSnoozeCount());

        CallLog callLog = callLogService.createCallLog(userId, request);

        return ResponseEntity.status(201).body(ApiResponse.success(callLog));
    }

    @Operation(
            summary = "통화 기록 조회",
            description = "사용자의 통화 기록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "통화 기록 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCallLogs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "시작 날짜 (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜 (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "오프셋 (0부터 시작)")
            @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "페이지 크기 (1-100)")
            @RequestParam(defaultValue = "20") int limit) {

        Long userId = userPrincipal.getUserId();
        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<CallLog> callLogPage = callLogService.getCallLogs(userId, startDate, endDate, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("callLogs", callLogPage.getContent());
        response.put("totalCount", callLogPage.getTotalElements());
        response.put("hasMore", callLogPage.hasNext());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}