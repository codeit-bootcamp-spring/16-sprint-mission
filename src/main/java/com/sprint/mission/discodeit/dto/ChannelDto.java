package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 채널 조회 결과를 전달하는 DTO입니다.
 * 채널 정보와 최근 메시지 시간, 비공개 채널 참여자 ID를 함께 담습니다.
 */
public record ChannelDto(
        UUID id,                         // 채널을 식별하는 ID
        ChannelType type,                // 공개 채널인지 비공개 채널인지 나타내는 종류
        String name,                     // 채널 이름; 비공개 채널에서는 null일 수 있음
        String description,              // 채널 설명; 비공개 채널에서는 null일 수 있음
        Instant createdAt,               // 채널이 생성된 시각
        Instant updatedAt,               // 채널 정보가 마지막으로 수정된 시각
        Instant lastMessageAt,           // 가장 최근 메시지의 생성 시각; 메시지가 없으면 null
        List<UUID> participantUserIds    // 비공개 채널 참여자 ID 목록; 공개 채널에서는 빈 목록
) {
}