package com.sprint.mission.discodeit.dto.request.channels;

public record PublicChannelUpdateRequest(

        String newName,
        String newDescription
) {
}
