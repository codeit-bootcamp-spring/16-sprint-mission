package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(@Schema(description = "가입한 사용자 이름", example = "demo-user") String username,
                           @Schema(description = "가입할 때 설정한 비밀번호", example = "demo-password") String password) {
}