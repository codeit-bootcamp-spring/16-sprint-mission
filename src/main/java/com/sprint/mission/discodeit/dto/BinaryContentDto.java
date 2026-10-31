package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContentDto {

    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final UUID userId;
    private final UUID messageId;

    public BinaryContentDto(
            UUID id,
            Instant createdAt,
            String fileName,
            String contentType,
            UUID userId,
            UUID messageId
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.fileName = fileName;
        this.contentType = contentType;
        this.userId = userId;
        this.messageId = messageId;
    }
}
