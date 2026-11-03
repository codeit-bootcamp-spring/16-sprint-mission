package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ChannelUpdateDto(
    UUID id,
    String name,
    String description
){}
