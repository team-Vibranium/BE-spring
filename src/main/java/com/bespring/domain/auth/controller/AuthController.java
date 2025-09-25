package com.bespring.domain.auth.controller;

import com.bespring.domain.auth.dto.request.LoginRequest;
import com.bespring.domain.auth.dto.request.RegisterRequest;
import com.bespring.domain.auth.dto.response.AuthResponse;
import com.bespring.domain.auth.service.AuthService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "회원가입, 로그인, 로그아웃 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"email\": \"user@example.com\", \"password\": \"password123\", \"nickname\": \"알람마스터\"}"
                            )
                    )
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 이메일"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request: email={}", request.getEmail());

        AuthResponse response = authService.register(request);

        log.info("User registered successfully: userId={}", response.getUser().getId());
        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"email\": \"user@example.com\", \"password\": \"password123\"}"
                            )
                    )
            )
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request: email={}", request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("User logged in successfully: userId={}", response.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 사용자를 로그아웃합니다. JWT 토큰을 무효화합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String token = jwtUtil.extractTokenFromHeader(bearerToken);

        if (token != null) {
            authService.logout(token);
            log.info("User logged out successfully");
        }

        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }
}