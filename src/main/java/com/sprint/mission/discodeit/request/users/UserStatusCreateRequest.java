package com.sprint.mission.discodeit.request.users;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(UUID userId,
                                      Instant lastActiveAt) {
}