package com.bespring.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    private String nickname;

    @Size(max = 50, message = "아바타 ID는 50자를 초과할 수 없습니다")
    private String selectedAvatar;
}