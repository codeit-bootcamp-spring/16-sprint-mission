package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID binaryContentId,
        String fileName,
        String contentType
        // byte[] bytes
) {
}
