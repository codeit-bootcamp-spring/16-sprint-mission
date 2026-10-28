package com.sprint.mission.discodeit.entity;


import java.io.Serial;

public class BinaryContent extends BaseEntity {
    private final long size;
    private final byte[] bytes;

    @Serial
    private static final long serialVersionUID = 1L;

    public BinaryContent(byte[] bytes) {
        super();
        this.size = bytes.length;
        this.bytes = bytes.clone();
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
