package com.bespring.domain.call.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "대화 발화 정보")
public class Utterance {

    @Schema(description = "발화자", example = "user", allowableValues = {"user", "assistant", "system"}, required = true)
    @NotBlank(message = "발화자는 필수입니다")
    private String speaker;

    @Schema(description = "발화 내용", example = "안녕하세요, 일어나세요!", required = true)
    @NotBlank(message = "발화 내용은 필수입니다")
    private String text;

    @Schema(description = "발화 시간", example = "2024-01-15T07:30:05", required = true)
    @NotNull(message = "발화 시간은 필수입니다")
    private LocalDateTime timestamp;

    public static Utterance createUser(String text) {
        return new Utterance("user", text, LocalDateTime.now());
    }

    public static Utterance createAssistant(String text) {
        return new Utterance("assistant", text, LocalDateTime.now());
    }

    public static Utterance createSystem(String text) {
        return new Utterance("system", text, LocalDateTime.now());
    }
}