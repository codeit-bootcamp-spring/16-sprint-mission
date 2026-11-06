package com.sprint.mission.discodeit.dto.request.channels;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(List<UUID> memberIds) { }