package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public record ChannelCreateRequest(
        ChannelType type,
        // 이 걸, List<User> 로 하는 사람이 어딧니 ㅡㅡ
        List<UUID> userList,
        String name,
        String description
) {
}
