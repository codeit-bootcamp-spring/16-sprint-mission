package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {
    private final String fileName;
    private final byte[] content;

    public BinaryContent(String fileName, byte[] content) {
        super();

        this.fileName = fileName;
        this.content = content;
    }
}
