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
    private final Long size;
    private final String contentType;
    private final byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes.clone();
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
