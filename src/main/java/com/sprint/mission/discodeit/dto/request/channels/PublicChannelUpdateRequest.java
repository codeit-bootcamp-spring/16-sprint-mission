package com.sprint.mission.discodeit.dto.request.channels;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
        @NotBlank(message = "채널 이름은 필수입니다.")
        String channelName,

        String description
) {
}
