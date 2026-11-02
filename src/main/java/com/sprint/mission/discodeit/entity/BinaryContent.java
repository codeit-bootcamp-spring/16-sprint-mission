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

    public BinaryContent(
            String fileName,
            String contentType,
            byte[] data
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data.clone();
    }

    // 데이터가 외부에서 변경되지 않도록 복사해서 반환
    public byte[] getData() {
        return data.clone();
    }
}
