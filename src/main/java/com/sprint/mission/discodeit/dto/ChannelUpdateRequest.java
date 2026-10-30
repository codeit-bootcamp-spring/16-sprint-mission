package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId, // 수정할 채널을 찾기 위한 ID
        String name, // 바꿀 채널 이름
        String description // 바꿀 채널 설명
) {
}