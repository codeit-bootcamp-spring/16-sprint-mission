package com.sprint.mission.discodeit.dto.channel;

public record ChannelUpdateRequest(
        String name,
        String description
) {
    public ChannelUpdateRequest {
        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 공백일 수 없습니다.");
        }
    }
}
