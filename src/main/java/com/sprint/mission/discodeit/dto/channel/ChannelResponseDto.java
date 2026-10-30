package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ChannelResponseDto {
    private UUID id;
    private ChannelType type;
    private String channelName;
    private String description;
    private UUID creatorId;
    private Instant createdAt;
    private Instant updatedAt;

    // 가장 최근 메시지의 시간 정보
    private Instant lastMessageAt;

    // 참여한 User의 id 정보
    private List<UUID> participantIds;
}
