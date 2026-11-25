package com.sprint.mission.discodeit.dto.channel;

import java.util.List;

public record ChannelListResponse(
        List<ChannelResponse> channels
) {
}
