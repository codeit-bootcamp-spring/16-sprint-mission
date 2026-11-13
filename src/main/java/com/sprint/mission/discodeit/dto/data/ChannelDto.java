package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    @JsonProperty("channel-id")
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UUID> participantIds,
    @JsonFormat(pattern = "yyyy-MM-dd")
    Instant lastMessageAt
) {

}
