package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentCreateRequest {
    private final String fileName;
    private final String contentType;
    private final byte[] data;
    private final UUID userId;
    private final UUID messageId;

    public BinaryContentCreateRequest(
            String fileName,
            String contentType,
            byte[] data,
            UUID userId,
            UUID messageId
    ) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.userId = userId;
        this.messageId = messageId;
    }
}
