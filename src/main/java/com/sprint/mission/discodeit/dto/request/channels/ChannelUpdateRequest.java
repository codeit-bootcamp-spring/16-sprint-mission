package com.sprint.mission.discodeit.dto.request.channels;

import java.util.UUID;

public record ChannelUpdateRequest(UUID channelId,
                                   String channelName) { }