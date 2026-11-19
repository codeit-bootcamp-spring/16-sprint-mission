package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 목록 조회 항목")
public record FindChannelResponse(
        @Schema(description = "채널 id", example = "22222222-2222-4222-8222-222222222222")
        UUID id,
        @Schema(description = "채널 종류", example = "PUBLIC")
        ChannelType type,
        @Schema(description = "채널명 (비공개 채널이면 null)", example = "공지사항")
        String name,
        @Schema(description = "채널 설명 (비공개 채널이면 null)", example = "공지사항을 올리는 채널입니다.")
        String description,
        @Schema(description = "가장 최근 메시지의 작성 시각 (메시지가 없으면 null)", example = "2026-11-13T00:30:00Z")
        Instant lastMessageAt,
        @Schema(description = "참여자 id 목록 (비공개 채널만 채워짐)", example = "[\"11111111-1111-4111-8111-111111111111\"]")
        List<UUID> participantIds
) {

    public static FindChannelResponse from(Channel channel, Instant lastMessageAt, List<UUID> participantIds) {
        return new FindChannelResponse(channel.getId(), channel.getType(), channel.getName(),
                channel.getDescription(), lastMessageAt, participantIds);
    }
}
