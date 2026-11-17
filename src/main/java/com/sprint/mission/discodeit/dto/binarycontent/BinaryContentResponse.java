package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID binaryContentId,
        String fileName,
        String contentType,
        byte[] bytes
) {
}
