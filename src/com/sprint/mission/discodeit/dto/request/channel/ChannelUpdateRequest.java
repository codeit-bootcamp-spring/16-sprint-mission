package com.sprint.mission.discodeit.dto.request.channel;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID id,
        String name,
        String description
) {
}