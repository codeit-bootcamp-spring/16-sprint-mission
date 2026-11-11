package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID=1L;

    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final Long fileSize;
    private final String fileUrl;

    public BinaryContent(String fileName, String contentType, Long fileSize, String fileUrl) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }

}
