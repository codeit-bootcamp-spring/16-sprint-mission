package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;

public record UserProfileImageCreateRequest(
        BinaryContentCreateRequest binaryContentCreateRequest
) {
}
