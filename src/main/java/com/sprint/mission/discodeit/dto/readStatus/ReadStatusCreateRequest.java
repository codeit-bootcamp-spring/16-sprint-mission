package com.sprint.mission.discodeit.dto.readStatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID channelId,
        UUID userId
) {
    public ReadStatusCreateRequest {
        if (channelId == null) {
            throw new NullPointerException("메세지 확인 기록을 생성할 채널 아이디 값이 없습니다.");
        }
        if (userId == null) {
            throw new NullPointerException("메세지 확인 기록을 생성할 유저 아이디 값이 없습니다.");
        }
    }
}
