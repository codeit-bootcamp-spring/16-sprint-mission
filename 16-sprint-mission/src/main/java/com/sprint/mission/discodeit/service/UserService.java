package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService extends UserUpdate {

    User create(String id, String email, String password);

    User findById(String id);

    List<User> findAll();

    User delete(String id);
}
