package com.sprint.mission.discodeit.dto.binaryContent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateRequest(
        @NotBlank
        String fileName,

        @NotEmpty
        byte[] content
) {
}
