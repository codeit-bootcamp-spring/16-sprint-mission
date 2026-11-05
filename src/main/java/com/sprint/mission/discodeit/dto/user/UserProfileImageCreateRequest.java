package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;

public record UserProfileImageCreateRequest(
        BinaryContentCreateRequest binaryContentCreateRequest
) {
    public UserProfileImageCreateRequest {
        //TODO: 바이트 데이터가 이미지인지 검증
    }
}
