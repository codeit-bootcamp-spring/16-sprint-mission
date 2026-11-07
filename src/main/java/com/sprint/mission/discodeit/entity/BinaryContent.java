package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public final class BinaryContent implements Serializable { // BinaryContent는 불변 객체를 생성함으로 클래스 앞에 final을 붙입니다.
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final byte[] bytes; // byte[]는 바이트 배열을 나타냅니다.

    // contentType은 추후에 enum을 사용하도록 구현하는 것이 좋습니다.
    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        /* 엔티티 검증 로직은 일단 사용하지않습니다.
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("");
        }
        if (bytes == null) {
            throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        }
         */

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
