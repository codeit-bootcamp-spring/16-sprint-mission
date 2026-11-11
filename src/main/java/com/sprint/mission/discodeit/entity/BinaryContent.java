package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {
    private final String fileName;
    private final String extension;
    private final byte[] content;

    public BinaryContent(String fileName, String extension, byte[] content) {
        super();

        this.fileName = fileName;
        this.extension = extension;
        this.content = content;
    }
}
