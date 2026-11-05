package com.sprint.mission.discodeit.dto.userStatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId,
        Instant lastActiveTime
) {
    public UserStatusCreateRequest {
        if (userId == null) {
            throw new NullPointerException("유저 접속 상태를 생성하는 유저 아이디의 값이 없습니다.");
        }
        if (lastActiveTime == null) {
            throw new NullPointerException("생성할 유저 접속 상태의 값이 없습니다.");
        }
    }
}
