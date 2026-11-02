package com.sprint.mission.discodeit.dto.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateByUserIdRequest(
        UUID userId,
        Instant lastActiveTime
) {
    public UserStatusUpdateByUserIdRequest {
        if (userId == null) {
            throw new NullPointerException("유저 접속 상태를 수정하는 유저 아이디의 값이 없습니다.");
        }
        if (lastActiveTime == null) {
            throw new NullPointerException("수정할 유저 접속 상태의 값이 없습니다.");
        }
    }
}
