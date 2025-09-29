package com.bespring.domain.call.controller;

import com.bespring.domain.call.dto.request.CallEndRequest;
import com.bespring.domain.call.dto.request.CallStartRequest;
import com.bespring.domain.call.dto.request.TranscriptRequest;
import com.bespring.domain.call.dto.response.CallDetailResponse;
import com.bespring.domain.call.dto.response.CallStartResponse;
import com.bespring.domain.call.service.CallService;
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

@Tag(name = "통화 관리", description = "음성 통화 생명주기 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CallController {

    private final CallService callService;

    @Operation(
            summary = "통화 시작",
            description = "새로운 음성 통화를 시작하고 CallLog를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "통화 시작 성공"
            )
    })
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<CallStartResponse>> startCall(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CallStartRequest request) {

        Long userId = userPrincipal.getUserId();
        log.info("Starting call for user: {}, sessionId: {}", userId, request.getSessionId());

        CallStartResponse response = callService.startCall(userId, request);

        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @Operation(
            summary = "대화 내용 저장",
            description = "OpenAI Realtime API 대화 내용을 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "대화 내용 저장 성공"
            )
    })
    @PostMapping("/{callId}/transcript")
    public ResponseEntity<ApiResponse<Void>> saveTranscript(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "통화 ID") @PathVariable Long callId,
            @Valid @RequestBody TranscriptRequest request) {

        Long userId = userPrincipal.getUserId();
        log.info("Saving transcript for call: {}, user: {}", callId, userId);

        callService.saveTranscript(userId, callId, request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "통화 종료",
            description = "음성 통화를 종료하고 최종 결과를 저장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "통화 종료 성공"
            )
    })
    @PostMapping("/{callId}/end")
    public ResponseEntity<ApiResponse<Void>> endCall(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "통화 ID") @PathVariable Long callId,
            @Valid @RequestBody CallEndRequest request) {

        Long userId = userPrincipal.getUserId();
        log.info("Ending call: {}, user: {}, result: {}", callId, userId, request.getResult());

        callService.endCall(userId, callId, request);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "통화 조회",
            description = "특정 통화의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "통화 조회 성공"
            )
    })
    @GetMapping("/{callId}")
    public ResponseEntity<ApiResponse<CallDetailResponse>> getCall(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "통화 ID") @PathVariable Long callId) {

        Long userId = userPrincipal.getUserId();
        log.info("Getting call details: {}, user: {}", callId, userId);

        CallDetailResponse response = callService.getCallDetail(userId, callId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}