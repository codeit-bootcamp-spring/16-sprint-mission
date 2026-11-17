package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Instant updatedAt,
    String username,
    String email,
    UUID profileId,
    Boolean online
) {

}
