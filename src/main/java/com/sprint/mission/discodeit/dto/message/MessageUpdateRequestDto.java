package com.sprint.mission.discodeit.dto.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageUpdateRequestDto {

    @Schema(example = "메시지 수정 내용 예시입니다.")
    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String newContent;
}