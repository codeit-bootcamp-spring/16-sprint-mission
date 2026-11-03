package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDto(
        UUID creatorId,
        List<UUID> userIds
) { }
