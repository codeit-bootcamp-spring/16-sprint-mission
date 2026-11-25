package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record UserActivityRequest(@Schema(description = "갱신할 마지막 활동 시각", example = "2026-11-17T09:00:00Z",
                                    requiredMode = Schema.RequiredMode.REQUIRED) Instant newLastActiveAt) {
}