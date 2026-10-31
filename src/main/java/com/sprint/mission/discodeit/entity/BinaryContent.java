package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;

    private String fileName;
    private String contentType;
    private byte[] data;

    private UUID userId;
    private UUID messageId;

    public BinaryContent(
            String fileName,
            String contentType,
            byte[] data,
            UUID userId,
            UUID messageId
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.userId = userId;
        this.messageId = messageId;
    }
}