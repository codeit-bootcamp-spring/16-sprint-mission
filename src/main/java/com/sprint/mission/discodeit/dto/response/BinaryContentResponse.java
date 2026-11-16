package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContent;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "파일 정보")
public record BinaryContentResponse(
        @Schema(description = "파일 id", example = "66666666-6666-4666-8666-666666666666")
        UUID id,
        @Schema(description = "생성 시각", example = "2026-11-13T00:00:00Z")
        String createdAt,
        @Schema(description = "파일명", example = "hello.txt")
        String fileName,

        @Schema(description = "파일 크기(바이트)", example = "5")
        Long size,
        @Schema(description = "파일 형식(MIME 타입)", example = "text/plain")
        String contentType,
        @Schema(description = "파일 내용 (base64로 인코딩된 문자열)", example = "SGVsbG8=")
        byte[] bytes
) {

    public static BinaryContentResponse from(BinaryContent content) {
        return new BinaryContentResponse(content.getId(), content.getCreatedAt().toString(),
                content.getFileName(), content.getSize(), content.getContentType(), content.getBytes());
    }
}
