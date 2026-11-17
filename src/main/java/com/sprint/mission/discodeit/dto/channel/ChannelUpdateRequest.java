package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateRequest(
        String newName,
        String newDescription
) {
}
