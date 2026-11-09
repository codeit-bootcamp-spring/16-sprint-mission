package com.sprint.mission.discodeit.dto.response.binarycontent;

import java.util.UUID;

public record BinaryContentDownloadDto(
        UUID id,
        String fileName,
        String contentType,
        byte[] data
) {
    public BinaryContentDownloadDto {
        data = data.clone();
    }

    @Override
    public byte[] data() {
        return data.clone();
    }
}