package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(@Schema(hidden = true) UUID readStatusId,
                                      @Schema(description = "갱신할 마지막 읽음 시각", example = "2026-11-17T09:05:00Z") Instant newLastReadAt) {
}