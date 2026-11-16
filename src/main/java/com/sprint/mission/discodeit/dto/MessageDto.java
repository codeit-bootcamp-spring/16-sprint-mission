package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** 메시지 생성 결과를 서비스 외부에 전달하는 DTO입니다. */
public record MessageDto(
        UUID id,
        String content,
        UUID authorId,
        UUID channelId,
        List<UUID> attachmentIds,
        Instant createdAt,
        Instant updatedAt
) {
    public MessageDto {
        attachmentIds = List.copyOf(attachmentIds);
    }
}
