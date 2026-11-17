package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.UUID;

public interface UserStatusService {

    void updateByUserId(UUID userId, Instant lastActiveAt);
}