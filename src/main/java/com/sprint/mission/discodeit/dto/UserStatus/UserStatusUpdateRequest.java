package com.sprint.mission.discodeit.dto.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        Instant lastActiveTime
) {
    public UserStatusUpdateRequest {
        if (id == null) {
            throw new NullPointerException("수정할 유저 접속 상태 아이디의 값이 없습니다.");
        }
        if (lastActiveTime == null) {
            throw new NullPointerException("수정할 유저 접속 상태의 값이 없습니다.");
        }
    }
}
