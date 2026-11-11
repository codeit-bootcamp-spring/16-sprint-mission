package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentResponseDto(
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        Long fileSize,
        String fileUrl
) {
    // 엔티티 객체를 DTO로 변환
    public static BinaryContentResponseDto from(BinaryContent binaryContent) {
        return new BinaryContentResponseDto(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getFileSize(),
                binaryContent.getFileUrl()
        );
    }
}
