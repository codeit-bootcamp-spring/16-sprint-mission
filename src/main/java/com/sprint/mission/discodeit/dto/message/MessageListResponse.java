package com.sprint.mission.discodeit.dto.message;

import java.util.List;

public record MessageListResponse(
        List<MessageResponse> messages
) {
}
