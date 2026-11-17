package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 목록의 개별 항목")
public record ChannelDto(
        @Schema(description = "채널 ID")
        UUID id,

        @Schema(description = "채널 유형", example = "PUBLIC")
        ChannelType type,

        @Schema(description = "채널 이름", example = "general")
        String name,

        @Schema(description = "채널 설명", example = "자유롭게 대화하는 채널")
        String description,

        @Schema(description = "비공개 채널의 참여 사용자 ID 목록. 공개 채널에서는 빈 목록")
        List<UUID> participantIds,

        @Schema(description = "채널의 가장 최근 메시지 생성 시각")
        Instant lastMessageAt
) {
}