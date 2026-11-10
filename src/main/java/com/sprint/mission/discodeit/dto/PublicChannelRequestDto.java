package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelRequestDto(
        String name,
        String description
) {}
