package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponse(
        UUID id,
        Instant createdAt,

        @Schema(example = "cat.jpg")
        String fileName,

        @Schema(example = "4685")
        Long size,

        @Schema(example = "image/jpeg")
        String contentType,

        @Schema(example = "ZXhhbXBsZQ==")
        byte[] bytes
) {
    public static BinaryContentResponse from(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getSize(),
                binaryContent.getContentType(),
                binaryContent.getContent()
        );
    }
}
