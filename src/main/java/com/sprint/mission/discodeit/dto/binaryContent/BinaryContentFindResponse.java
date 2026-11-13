package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentFindResponse(
        UUID id,
        String fileName,
        byte[] content
) {
    public static BinaryContentFindResponse from(BinaryContent binaryContent) {
        return new BinaryContentFindResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContent()
        );
    }
}
