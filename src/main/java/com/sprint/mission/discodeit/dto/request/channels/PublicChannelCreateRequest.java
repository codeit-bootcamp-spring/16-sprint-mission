package com.sprint.mission.discodeit.dto.request.channels;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지

public record PublicChannelCreateRequest(
        @NotBlank(message = "채널 이름은 필수입니다.")
        String name,

        String description
) {
}
