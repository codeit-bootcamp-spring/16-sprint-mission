package com.sprint.mission.discodeit.dto.userstatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UserStatusResponseDto {

    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean online;
}