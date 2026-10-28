package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);
    User findById(UUID id);
    List<User> findAll();
    List<User> findAllByChannelId(UUID channelId);
    User update(UUID id, String username, String email, String password);
    void delete(UUID id);

    void addChannel(UUID id, UUID channelId);
    void removeChannel(UUID id, UUID channelId);
    void addMessage(UUID id, UUID messageId);
    void removeMessage(UUID id, UUID messageId);
}
