package com.sprint.mission.discodeit.dto.readstatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class ReadStatusResponseDto {

    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
    private Instant createdAt;
    private Instant updatedAt;
}
