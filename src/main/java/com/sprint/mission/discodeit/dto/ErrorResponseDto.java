package com.sprint.mission.discodeit.dto;

import java.time.Instant;

public record ErrorResponseDto(
   int status,
   String message,
   Instant timestamp
) {}
