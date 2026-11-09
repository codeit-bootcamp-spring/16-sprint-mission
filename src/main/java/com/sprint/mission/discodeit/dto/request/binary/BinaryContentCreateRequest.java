package com.sprint.mission.discodeit.dto.request.binary;

import jakarta.validation.constraints.NotBlank; // null, 빈 문자열, 공백 방지
import jakarta.validation.constraints.NotNull; // null 값 방지
import jakarta.validation.constraints.PositiveOrZero;

public record BinaryContentCreateRequest(

        @NotBlank(message = "파일 이름은 필수입니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 필수입니다.")
        String contentType,

        @NotNull(message = "파일 크기는 필수입니다.")
        @PositiveOrZero(message = "파일 크기는 0 이상이어야 합니다.")
        Long size,

        @NotNull(message = "파일 데이터는 필수입니다.")
        byte[] bytes) { }