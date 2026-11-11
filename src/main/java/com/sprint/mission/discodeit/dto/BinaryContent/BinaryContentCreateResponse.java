package com.sprint.mission.discodeit.dto.BinaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentCreateResponse(
        UUID id,
        String fileName,
        String extension
) {
    public static BinaryContentCreateResponse from(BinaryContent binaryContent) {
        return new BinaryContentCreateResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getExtension()
        );
    }
}
