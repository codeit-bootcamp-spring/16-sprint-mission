package com.sprint.mission.discodeit.dto.userStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        Instant lastActiveTime
) {
    public UserStatusUpdateRequest {
        if (lastActiveTime == null) {
            throw new NullPointerException("수정할 유저 접속 상태의 값이 없습니다.");
        }
    }
}
