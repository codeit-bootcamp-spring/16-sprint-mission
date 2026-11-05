package com.sprint.mission.discodeit.dto.binaryContent;

import jakarta.validation.constraints.NotBlank;

public record BinaryContentCreateRequest(
        @NotBlank
        String fileName,

        @NotBlank
        byte[] content
) {
}
