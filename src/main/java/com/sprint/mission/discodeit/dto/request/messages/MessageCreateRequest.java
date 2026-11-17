package com.sprint.mission.discodeit.dto.request.messages;

import com.sprint.mission.discodeit.dto.request.binary.BinaryContentCreateRequest;

import jakarta.validation.Valid; // DTO의 유효성 검사를 실행하도록 지정
import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지
import jakarta.validation.constraints.NotNull; // null 값 방지

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (

        @NotBlank(message = "메시지 내용은 필수입니다.")
        String content,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId // ,

        // List<@Valid BinaryContentCreateRequest> attachments
) { }