package com.sprint.mission.discodeit.dto.request.otherRequest;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(UUID userId,
                                      Instant lastActiveAt) { }