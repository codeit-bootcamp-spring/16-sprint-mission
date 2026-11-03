
package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record AuthLoginResponse(
        UUID id,
        String username,
        String email,
        UUID profileImageId
) {
}
