package com.sprint.mission.discodeit.dto.request.status;

import java.util.UUID;

public record ReadStatusCreateRequest(UUID userId,
                                      UUID channelId) { }