package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "BinaryContentDto")
public record BinaryContentDto(
        UUID id,
        String fileName,
        Long size,
        String contentType
) {
}
