package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UpdateChannelRequest(
        UUID id,
        String newName,
        String newDescription
) {
}
