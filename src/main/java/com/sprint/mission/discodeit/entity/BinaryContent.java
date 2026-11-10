package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final byte[] data;

    public BinaryContent(byte[] data) {
        Validation.validateNull(data, "data");

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.data = data.clone();
    }

    public byte[] getData() {
        return data.clone();
    }
}