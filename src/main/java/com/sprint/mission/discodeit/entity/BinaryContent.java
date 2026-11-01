package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public final class BinaryContent implements Serializable { // BinarayContetn는 불변 객체를 생성함으로 클래스 앞에 final을 붙입니다.
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final byte[] bytes; // byte[]는 바이트 배열을 나타냅니다.

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
