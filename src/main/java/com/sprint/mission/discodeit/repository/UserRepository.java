package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User findById(UUID id);

    List<User> findAll();

    void save(User user);

    void delete(UUID id);
}
