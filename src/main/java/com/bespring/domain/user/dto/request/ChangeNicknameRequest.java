package com.bespring.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeNicknameRequest {

    @NotBlank(message = "새 닉네임은 필수입니다")
    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    private String newNickname;
}