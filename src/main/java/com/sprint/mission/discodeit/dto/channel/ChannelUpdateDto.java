package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateDto(
        UUID id,
        String name,
        String description
) {
}
