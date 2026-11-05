package com.sprint.mission.discodeit.dto.channel;

public record ChannelPublicCreateRequest(
        String name,
        String description
) {
    public ChannelPublicCreateRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 공백일 수 없습니다.");
        }
    }
}
