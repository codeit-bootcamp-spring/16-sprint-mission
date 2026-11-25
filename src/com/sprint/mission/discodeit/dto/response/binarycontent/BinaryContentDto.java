package com.sprint.mission.discodeit.dto.response.binarycontent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        Instant createdAt,
        String fileName,
        long size,
        String contentType,
        byte[] bytes
) {
    public BinaryContentDto {
        bytes = bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }
}