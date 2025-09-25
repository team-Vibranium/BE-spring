package com.bespring.domain.user.controller;

import com.bespring.domain.auth.dto.response.AuthResponse;
import com.bespring.domain.user.dto.request.UpdateUserRequest;
import com.bespring.domain.user.service.UserService;
import com.bespring.global.dto.ApiResponse;
import com.bespring.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 관리", description = "사용자 정보 조회, 수정, 삭제 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.UserInfo.class))
            )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();
        AuthResponse.UserInfo userInfo = userService.getUserInfo(userId);

        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "현재 로그인한 사용자의 정보를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.UserInfo.class))
            )
    })
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> updateMyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateUserRequest request) {

        Long userId = userPrincipal.getUserId();

        log.info("Update user info request: userId={}, nickname={}, avatar={}",
                userId, request.getNickname(), request.getSelectedAvatar());

        AuthResponse.UserInfo updatedUserInfo = userService.updateUserInfo(
                userId, request.getNickname(), request.getSelectedAvatar());

        log.info("User info updated successfully: userId={}", userId);
        return ResponseEntity.ok(ApiResponse.success(updatedUserInfo));
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> changeNickname(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody com.bespring.domain.user.dto.request.ChangeNicknameRequest request) {

        Long userId = userPrincipal.getUserId();

        log.info("Change nickname request: userId={}, newNickname={}", userId, request.getNewNickname());

        AuthResponse.UserInfo updatedUserInfo = userService.changeNickname(userId, request.getNewNickname());

        log.info("Nickname changed successfully: userId={}", userId);
        return ResponseEntity.ok(ApiResponse.success(updatedUserInfo, "닉네임이 변경되었습니다."));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody com.bespring.domain.user.dto.request.ChangePasswordRequest request) {

        Long userId = userPrincipal.getUserId();

        // 비밀번호 확인 검증
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("VALIDATION_ERROR", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."));
        }

        log.info("Change password request: userId={}", userId);

        userService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());

        log.info("Password changed successfully: userId={}", userId);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공"
            )
    })
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getUserId();

        log.info("Delete user account request: userId={}", userId);

        userService.deleteUser(userId);

        log.info("User account deleted successfully: userId={}", userId);
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴가 완료되었습니다."));
    }
}