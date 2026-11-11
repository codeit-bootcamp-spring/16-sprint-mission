package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
        UUID authorId,
        UUID channelId,
        String content
) {}
