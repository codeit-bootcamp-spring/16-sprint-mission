package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends SaveObject<User> {
    // User saveUser(User user);
    List<User> getAllUsers();
    User getUser(UUID id);
    void removeUser(UUID id);
}
