package com.sprint.mission.discodeit.request.channels;

public record PublicChannelUpdateRequest(

        String newName,
        String newDescription
) {
}
