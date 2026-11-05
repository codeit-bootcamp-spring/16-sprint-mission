package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserProfileImageUpdateRequest(
        @NotBlank
        UUID userId,

        BinaryContentCreateRequest binaryContentCreateRequest
) {
}
