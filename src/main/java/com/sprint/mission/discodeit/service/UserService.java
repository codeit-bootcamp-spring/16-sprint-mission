package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String username);
    List<User> findAll();
    User findById(UUID id);
    User updateUsername(UUID id, String username);
    void deleteUser(UUID id);
}
