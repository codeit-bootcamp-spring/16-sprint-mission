package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        ChannelType type,
        String name,                     // 채널 이름; 비공개 채널에서는 null일 수 있음
        String description,              // 채널 설명; 비공개 채널에서는 null일 수 있음
        Instant createdAt,
        Instant updatedAt,
        Instant lastMessageAt,           // 가장 최근 메시지의 생성 시각; 메시지가 없으면 null
        List<UUID> participantUserIds    // 비공개 채널 참여자 ID 목록; 공개 채널에서는 빈 목록
) {
}