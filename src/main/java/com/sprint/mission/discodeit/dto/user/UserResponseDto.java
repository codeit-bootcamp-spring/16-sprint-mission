package com.sprint.mission.discodeit.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean online;
}
