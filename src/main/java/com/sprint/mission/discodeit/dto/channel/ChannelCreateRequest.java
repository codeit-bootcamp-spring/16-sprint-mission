package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequest(

        @NonNull
        ChannelType type,
        List<UUID> userList,
        String name,
        String description
) {
}
