package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, User user);
    void delete(UUID id);

    boolean addMessage(UUID id, UUID messageId);
    boolean removeMessage(UUID id, UUID messageId);

    boolean addChannel(UUID id, UUID channelId);
    boolean removeChannel(UUID id, UUID channelId);
}
