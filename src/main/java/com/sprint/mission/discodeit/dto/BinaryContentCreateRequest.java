package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class BinaryContentCreateRequest {

    private final String fileName;
    private final String contentType;
    private final byte[] data;

    public BinaryContentCreateRequest(
            String fileName,
            String contentType,
            byte[] data
    ) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data.clone();
    }
}
