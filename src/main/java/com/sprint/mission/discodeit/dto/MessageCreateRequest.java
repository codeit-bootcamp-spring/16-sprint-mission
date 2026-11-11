package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessageCreateRequest(String comment, UUID authorId,UUID channelId) {
}
