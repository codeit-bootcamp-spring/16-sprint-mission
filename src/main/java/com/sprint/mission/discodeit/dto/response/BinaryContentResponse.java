package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponse(
        UUID contentId,
        String filename,
        Long size,
        String contentType,
        byte[] bytes
) {

    public static BinaryContentResponse from(BinaryContent content) {
        return new BinaryContentResponse(content.getId(), content.getFileName(), content.getSize(),
                content.getContentType(), content.getBytes());
    }
}
