package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;

    private String fileName;
    private byte[] data;
    private String contentType;


    public BinaryContent(String fileName, byte[] data, String contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.data = data;
        this.contentType = contentType;
    }
}
