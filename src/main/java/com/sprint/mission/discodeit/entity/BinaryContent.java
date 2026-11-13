package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@JsonPropertyOrder({
        "id",
        "createdAt",
        "fileName",
        "size",
        "contentType",
        "bytes"
})
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final Long size;
    private final String contentType;
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        Validation.validateNull(fileName, "fileName");
        Validation.validateNull(contentType, "contentType");
        Validation.validateNull(bytes, "bytes");

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.size = (long) bytes.length;
        this.contentType = contentType;
        this.bytes = bytes.clone();
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}