package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChannelUpdateRequest {
    private final UUID channelId;
    private final String name;
    private final String description;

    public ChannelUpdateRequest(
            UUID channelId,
            String name,
            String description
    ) {
        this.channelId = channelId;
        this.name = name;
        this.description = description;
    }
}
