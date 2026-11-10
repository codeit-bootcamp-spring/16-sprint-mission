package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record LoginResponse(UUID id, String name, String email,UUID profileId) {
}
