package com.sprint.mission.discodeit.dto.request.status;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(UUID userId,
                                      UUID channelId) { }