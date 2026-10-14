package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String username);
    List<User> findAll();
    User findById(UUID id);
    void updateUsername(UUID id, String username);
    boolean deleteUser(UUID id);
}
