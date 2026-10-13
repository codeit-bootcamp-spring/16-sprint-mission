package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserUpdate {

    User create(String userName, String email, String password);

    User findById(UUID id);

    User findByName(String userName);

    List<User> findAll();

    User delete(UUID id);
}
