package com.bespring.domain.call.dto.request;

import com.bespring.domain.call.dto.Utterance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "대화 내용 저장 요청")
public class TranscriptRequest {

    @Schema(description = "대화 내용 리스트", required = true)
    @NotEmpty(message = "대화 내용은 필수입니다")
    @Valid
    private List<Utterance> conversation;
}