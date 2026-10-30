package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
        UUID id, Instant createdAt,
        String fileName, String contentType, long size, byte[] bytes
) {}
