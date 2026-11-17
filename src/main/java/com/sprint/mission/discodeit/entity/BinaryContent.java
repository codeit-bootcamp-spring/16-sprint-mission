package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "profile.png")
    private final String fileName;

    @Schema(example = "1024")
    private final Long size;

    @Schema(example = "image/png")
    private final String contentType;

    @Schema(example = "aGVsbG8=")
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