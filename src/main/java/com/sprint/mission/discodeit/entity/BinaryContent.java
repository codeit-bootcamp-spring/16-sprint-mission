package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final Instant createdAt;
    private final String filename;
    private final Long size;
    private final byte[] bytes;

    public BinaryContent(String filename, Long size, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.filename = filename;
        this.size = size;
        this.bytes = bytes;
    }
}
