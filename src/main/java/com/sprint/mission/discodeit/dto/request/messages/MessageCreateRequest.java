package com.sprint.mission.discodeit.dto.request.messages;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지
import jakarta.validation.constraints.NotNull; // null 값 방지

import java.util.UUID;

public record MessageCreateRequest(

        @NotBlank(message = "메시지 내용은 필수입니다.")
        String content,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId) {
}
