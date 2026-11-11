package com.sprint.mission.discodeit.dto.binarycontent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record BinaryContentCreateRequest(
        @NotBlank
        String fileName,
        @NotBlank
        String contentType,
        @NotEmpty
        byte[] bytes
) {
}
