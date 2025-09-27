package com.bespring.domain.mission.controller;

import com.bespring.domain.mission.dto.request.MissionResultRequest;
import com.bespring.domain.mission.entity.MissionResult;
import com.bespring.domain.mission.service.MissionService;
import com.bespring.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "미션 결과", description = "알람 미션 결과 저장 및 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/mission-results")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(
            summary = "미션 결과 저장",
            description = "알람 미션의 성공/실패 결과를 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "미션 결과 저장 성공",
                    content = @Content(schema = @Schema(implementation = MissionResult.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MissionResult>> saveMissionResult(
            @Valid @RequestBody MissionResultRequest request) {

        log.info("Save mission result request: callLogId={}, missionType={}, success={}",
                request.getCallLogId(), request.getMissionType(), request.getSuccess());

        MissionResult missionResult = missionService.saveMissionResult(
                request.getCallLogId(),
                request.getMissionType(),
                request.getSuccess()
        );

        return ResponseEntity.status(201).body(ApiResponse.success(missionResult));
    }

    @Operation(
            summary = "미션 결과 조회",
            description = "특정 통화 기록에 대한 미션 결과를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "미션 결과 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<MissionResult>>> getMissionResults(
            @Parameter(description = "통화 기록 ID")
            @RequestParam(required = true) Long callLogId) {

        List<MissionResult> missionResults = missionService.getMissionResults(callLogId);

        return ResponseEntity.ok(ApiResponse.success(missionResults));
    }
}