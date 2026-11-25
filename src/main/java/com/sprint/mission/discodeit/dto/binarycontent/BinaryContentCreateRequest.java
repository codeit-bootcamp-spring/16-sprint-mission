package com.sprint.mission.discodeit.dto.binarycontent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;

public record BinaryContentCreateRequest(
        @NotBlank
        @Schema(description = "파일 이름", example = "sample.txt")
        String fileName,

        @NonNull
        @Schema(description = "파일 크기(바이트)", example = "5")
        Long size,

        @NotBlank
        @Schema(description = "파일 MIME 타입", example = "text/plain")
        String contentType,

        @NotEmpty
        @Schema(
                description = "파일의 바이너리 데이터(Base64 인코딩)",
                example = "SGVsbG8="
        )
        byte[] bytes
) {
}
