package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User create(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    User update(UUID id, String name, String email);
    void deleteById(UUID id);
    void addChannel(UUID userId, UUID channelId);
    void removeChannel(UUID userId, UUID channelId);
}
