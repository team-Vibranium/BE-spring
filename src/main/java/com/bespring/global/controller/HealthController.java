package com.bespring.global.controller;

import com.bespring.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "헬스체크", description = "서버 상태 확인 API")
@RestController
public class HealthController implements HealthIndicator {

    @Operation(
            summary = "서버 상태 확인",
            description = "서버가 정상적으로 동작 중인지 확인합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "서버 정상 동작 중"
            )
    })
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("service", "AningCall Backend");
        healthData.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success(healthData, "서버가 정상적으로 동작 중입니다."));
    }

    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "AningCall Backend")
                .withDetail("timestamp", LocalDateTime.now())
                .build();
    }
}