package com.sprint.mission.discodeit.dto.binaryContent;

public record BinaryContentCreateRequest(
        String fileName,
        byte[] content
) {
    public BinaryContentCreateRequest {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 공백일 수 없습니다.");
        }
        if (content == null) {
            throw new NullPointerException("바이트 데이터가 없습니다.");
        }
    }
}
