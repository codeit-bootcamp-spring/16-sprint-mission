package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record ChannelUpdateRequest(
        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
