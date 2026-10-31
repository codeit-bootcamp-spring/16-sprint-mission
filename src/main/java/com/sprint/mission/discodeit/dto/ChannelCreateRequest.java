package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ChannelCreateRequest {
    private final ChannelType type;
    private final String name;
    private final String description;
    private final List<UUID> userIds;

    public ChannelCreateRequest(
            ChannelType type,
            String name,
            String description,
            List<UUID> userIds
    ) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.userIds = userIds;
    }
}
