package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ChannelCreateRequest(
        ChannelType type,
        @Nullable List<User> userList,
        @Nullable String name,
        @Nullable String description
) {
}
