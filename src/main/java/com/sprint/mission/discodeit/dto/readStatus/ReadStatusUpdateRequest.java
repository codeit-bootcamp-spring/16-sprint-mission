package com.sprint.mission.discodeit.dto.readStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        Instant lastReadTime
) {
    public ReadStatusUpdateRequest {
        if (lastReadTime == null) {
            throw new NullPointerException("갱신할 메세지 확인 기록의 값이 없습니다.");
        }
    }
}
