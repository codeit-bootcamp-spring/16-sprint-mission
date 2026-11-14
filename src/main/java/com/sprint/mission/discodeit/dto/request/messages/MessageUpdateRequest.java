package com.sprint.mission.discodeit.dto.request.messages;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지

public record MessageUpdateRequest(

        @NotBlank(message = "메시지 내용은 필수입니다.")
        String newContent) {
}
