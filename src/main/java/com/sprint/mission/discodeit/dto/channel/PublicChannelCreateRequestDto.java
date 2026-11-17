package com.sprint.mission.discodeit.dto.channel;

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
public class PublicChannelCreateRequestDto {

    @Schema(example = "채널 이름 예시")
    @NotBlank(message = "채널 이름은 필수입니다.")
    private String name;

    @Schema(example = "채널 설명 예시입니다.")
    private String description;
}