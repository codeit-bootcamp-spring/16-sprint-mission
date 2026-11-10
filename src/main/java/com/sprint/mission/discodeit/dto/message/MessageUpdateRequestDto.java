package com.sprint.mission.discodeit.dto.message;

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

    @NotBlank(message = "메시지 내용은 필수입니다.")
    private String content;
}