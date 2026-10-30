package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        long size,
        byte[] data
) {
    public BinaryContentCreateRequest {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 필수입니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("파일 유형은 필수입니다.");
        }

        if (size < 0) {
            throw new IllegalArgumentException("파일 크기는 음수일 수 없습니다.");
        }

        if (data == null) {
            throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        }

        if (size != data.length) {
            throw new IllegalArgumentException("파일 크기와 실제 데이터 크기가 일치하지 않습니다.");
        }

        data = data.clone();
    }

    @Override
    public byte[] data() {
        return data.clone();
    }
}