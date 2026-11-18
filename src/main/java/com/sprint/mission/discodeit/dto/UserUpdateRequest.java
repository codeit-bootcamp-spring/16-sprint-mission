package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserUpdateRequest(@Schema(hidden = true) UUID userId,
                                @Schema(description = "변경할 사용자 이름", example = "demo-user-new") String newUsername,
                                @Schema(description = "변경할 비밀번호", example = "new-demo-password") String newPassword,
                                @Schema(description = "변경할 이메일 주소", example = "new-demo@example.com") String newEmail) {
}
