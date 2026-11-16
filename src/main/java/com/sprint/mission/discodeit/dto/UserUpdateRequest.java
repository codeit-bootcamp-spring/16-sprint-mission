package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(UUID userId, String newUsername, String newPassword, String newEmail) {
}
