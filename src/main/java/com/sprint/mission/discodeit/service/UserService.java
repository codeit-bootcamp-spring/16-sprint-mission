package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create(String name);

    User updateName(UUID id, String name);

    Optional<User> findById(UUID id);

    List<User> findAll();

    void delete(UUID id);

    void validateNotDeleted(User user);
}
