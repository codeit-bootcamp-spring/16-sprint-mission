package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentCreateResponse(
        UUID id,
        String fileName
) {
    public static BinaryContentCreateResponse from(BinaryContent binaryContent) {
        return new BinaryContentCreateResponse(
                binaryContent.getId(),
                binaryContent.getFileName()
        );
    }
}
