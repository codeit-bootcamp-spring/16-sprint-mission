package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateMessageRequest(
        UUID id,
        String content
) {
}