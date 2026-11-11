package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
        @NotBlank
        String newName,

        @NotBlank
        String newDescription
) {
}
