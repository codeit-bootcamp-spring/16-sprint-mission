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
    private final long size;
    private final byte[] data;

    public BinaryContent(
            String fileName,
            String contentType,
            long size,
            byte[] data
    ) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 필수입니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("파일 유형은 필수입니다.");
        }

        if (size < 0) {
            throw new IllegalArgumentException("파일 크기는 음수일 수 없습니다.");
        }

        if (data == null) {
            throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        }

        if (size != data.length) {
            throw new IllegalArgumentException("파일 크기와 실제 데이터 크기가 일치하지 않습니다.");
        }

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.data = data.clone();
    }

    public byte[] getData() {
        return data.clone();
    }
}