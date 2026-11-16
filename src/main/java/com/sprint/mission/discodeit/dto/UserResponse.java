package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, Instant createdAt, Instant updatedAt,
                           String username, String email, UUID profileId) {
}