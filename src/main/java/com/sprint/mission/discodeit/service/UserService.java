package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.Map;
import java.util.UUID;

public interface UserService extends BaseEntityService<User> {
    User create(String name);

    User update(UUID id, String name);

    void addChannel(UUID id, Channel channel);

    void removeChannel(UUID userId, UUID channelId);
}
