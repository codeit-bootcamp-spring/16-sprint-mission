package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serial;

@Getter
public class BinaryContent extends BaseEntity {
    private final String fileName;
    private final String contentType;
    private final long size;
    private final byte[] bytes;

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        super();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = bytes.length;
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
