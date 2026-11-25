package com.sprint.mission.discodeit.dto.request.channel;

public record ChannelUpdateRequest(
        String newName,
        String newDescription
) {
}