package com.sprint.mission.discodeit.dto.request.channelRequest;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(List<UUID> memberIds) { }