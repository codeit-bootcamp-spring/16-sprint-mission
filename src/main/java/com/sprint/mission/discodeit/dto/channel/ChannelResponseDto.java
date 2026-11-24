package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChannelResponseDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        ChannelType type,

        @Schema(example = "채널 이름 예시")
        String name,

        @Schema(example = "채널 설명 예시입니다.")
        String description
) {
}