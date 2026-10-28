package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public final class BinaryContent implements Serializable {
    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = (bytes == null) ? new byte[0] : bytes.clone();
    }

    //얘는  Lombok한테 시키면  배열 그대로 반환해서 따로 만듬
    public byte[] getBytes() {
        return this.bytes.clone();
    }

}
