package com.sprint.mission.discodeit.dto.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID id,
        Instant lastReadTime
) {
    public ReadStatusUpdateRequest {
        if (id == null) {
            throw new NullPointerException("메세지 확인 기록을 수정할 채널 아이디 값이 없습니다.");
        }
        if (lastReadTime == null) {
            throw new NullPointerException("갱신할 메세지 확인 기록의 값이 없습니다.");
        }
    }
}
