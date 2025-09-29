package com.bespring.domain.alarm.controller;

import com.bespring.domain.alarm.dto.request.AlarmCreateRequest;
import com.bespring.domain.alarm.dto.response.AlarmResponse;
import com.bespring.domain.alarm.entity.UserAlarm;
import com.bespring.domain.alarm.service.UserAlarmService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "알람 관리", description = "사용자 알람 CRUD API")
@Slf4j
@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AlarmController {

    private final UserAlarmService userAlarmService;

    @Operation(
            summary = "알람 등록",
            description = "새로운 알람을 등록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "알람 등록 성공"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AlarmResponse>> createAlarm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AlarmCreateRequest request) {

        Long userId = userPrincipal.getUserId();
        log.info("Creating alarm for user {}: alarmTime={}, voice={}",
                userId, request.getAlarmTime(), request.getVoice());

        UserAlarm userAlarm = userAlarmService.createAlarm(
                userId,
                request.getAlarmTime(),
                request.getInstructions(),
                request.getVoice()
        );

        AlarmResponse response = AlarmResponse.from(userAlarm);
        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @Operation(
            summary = "사용자 알람 목록 조회",
            description = "해당 사용자의 알람 목록을 조회합니다. active 파라미터로 활성/비활성 알람 필터링 가능"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알람 목록 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<AlarmResponse>>> getUserAlarms(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "활성화 상태 필터 (true=활성화된 알람만, false=모든 알람)")
            @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {

        Long userId = userPrincipal.getUserId();
        log.info("Getting alarms for user {}: activeOnly={}", userId, activeOnly);

        List<UserAlarm> alarms = activeOnly
                ? userAlarmService.getActiveAlarms(userId)
                : userAlarmService.getAllAlarms(userId);

        List<AlarmResponse> responses = alarms.stream()
                .map(AlarmResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(
            summary = "특정 알람 조회",
            description = "특정 알람의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알람 조회 성공"
            )
    })
    @GetMapping("/{alarmId}")
    public ResponseEntity<ApiResponse<AlarmResponse>> getAlarm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "알람 ID") @PathVariable Long alarmId) {

        Long userId = userPrincipal.getUserId();
        log.info("Getting alarm {} for user {}", alarmId, userId);

        UserAlarm userAlarm = userAlarmService.getAlarm(userId, alarmId);
        AlarmResponse response = AlarmResponse.from(userAlarm);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "알람 삭제",
            description = "알람을 삭제합니다 (실제로는 비활성화 처리)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알람 삭제 성공"
            )
    })
    @DeleteMapping("/{alarmId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlarm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "알람 ID") @PathVariable Long alarmId) {

        Long userId = userPrincipal.getUserId();
        log.info("Deleting alarm {} for user {}", alarmId, userId);

        userAlarmService.deleteAlarm(userId, alarmId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "알람 수정",
            description = "기존 알람의 시간, 지시사항, 음성을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알람 수정 성공"
            )
    })
    @PutMapping("/{alarmId}")
    public ResponseEntity<ApiResponse<AlarmResponse>> updateAlarm(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "알람 ID") @PathVariable Long alarmId,
            @Valid @RequestBody AlarmCreateRequest request) {

        Long userId = userPrincipal.getUserId();
        log.info("Updating alarm {} for user {}: newTime={}",
                alarmId, userId, request.getAlarmTime());

        UserAlarm updatedAlarm = userAlarmService.updateAlarm(
                userId,
                alarmId,
                request.getAlarmTime(),
                request.getInstructions(),
                request.getVoice()
        );

        AlarmResponse response = AlarmResponse.from(updatedAlarm);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}