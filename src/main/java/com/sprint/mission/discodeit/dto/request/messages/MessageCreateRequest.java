package com.sprint.mission.discodeit.dto.request.messages;

import jakarta.validation.constraints.NotNull; // null 값 방지

import java.util.UUID;

public record MessageCreateRequest(

        String content,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId) {
}
