package com.sprint.mission.discodeit.dto.request.channelRequest;

import java.util.UUID;

public record ChannelUpdateRequest(UUID channelId,
                                   String channelName) { }