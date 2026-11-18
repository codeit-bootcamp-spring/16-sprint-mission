package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {
    private final String fileName;
    private final byte[] content;
    private final Long size;
    private final String contentType;

    public BinaryContent(String fileName, byte[] content, Long size, String contentType) {
        super();

        this.fileName = fileName;
        this.content = content;
        this.size = size;
        this.contentType = contentType;
    }
}
