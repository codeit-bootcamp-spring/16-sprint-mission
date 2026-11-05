package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserProfileImageUpdateRequest(
        UUID userId,
        String fileName,
        String extension,
        byte[] content
) {
    public UserProfileImageUpdateRequest {
        if (userId == null) {
            throw new NullPointerException("수정을 요청하는 유저 아이디가 없습니다.");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("수정할 파일 이름은 공백일 수 없습니다.");
        }
        //TODO: 바이트 데이터가 이미지인지 검증
    }
}
