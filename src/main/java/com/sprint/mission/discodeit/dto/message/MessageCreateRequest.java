package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageCreateRequest(
        UUID authorId,
        String content,
        UUID channelId
) {
    public MessageCreateRequest {
        if (authorId == null) {
            throw new NullPointerException("메세지를 작성할 유저 아이디의 값이 없습니다.");
        }
        if (channelId == null) {
            throw new NullPointerException("메세지를 작성하는 채널 아이디의 값이 없습니다.");
        }
    }
}
