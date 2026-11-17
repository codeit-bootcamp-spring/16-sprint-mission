package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ChannelDto(
        UUID id,
        ChannelType type,

        @Schema(example = "채널 이름 예시")
        String name,

        @Schema(example = "채널 설명 예시입니다.")
        String description,

        List<UUID> participantIds,
        Instant lastMessageAt
) {
}