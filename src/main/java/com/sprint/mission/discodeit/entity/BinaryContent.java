package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final byte[] data;

    private final UUID userId;
    private final UUID messageId;

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
        this.data = data.clone();
        this.userId = userId;
        this.messageId = messageId;
    }

    public byte[] getData() {
        return data.clone();
    }
}
