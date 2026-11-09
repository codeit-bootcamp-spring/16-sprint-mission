package com.sprint.mission.discodeit.dto.request.channels;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지
import jakarta.validation.constraints.NotNull; // null 값 방지

import java.util.UUID;

public record ChannelUpdateRequest(
        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @NotBlank(message = "채널 이름은 필수입니다.")
        String channelName,

        String description) { }