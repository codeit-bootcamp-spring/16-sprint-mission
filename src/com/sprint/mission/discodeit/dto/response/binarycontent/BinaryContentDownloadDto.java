package com.sprint.mission.discodeit.dto.response.binarycontent;

import java.util.UUID;

public record BinaryContentDownloadDto(
        UUID id,
        String fileName,
        String contentType,
        byte[] bytes
) {
    public BinaryContentDownloadDto {
        bytes = bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }
}