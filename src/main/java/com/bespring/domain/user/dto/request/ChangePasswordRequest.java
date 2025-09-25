package com.bespring.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 6, max = 100, message = "새 비밀번호는 6자 이상 100자 이하여야 합니다")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수입니다")
    private String confirmPassword;
}