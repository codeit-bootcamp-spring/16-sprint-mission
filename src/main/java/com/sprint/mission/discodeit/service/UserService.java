package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    void add(User user);

    User getById(UUID id);

    List<User> getAll();

    void update(UUID id, String username);

    void delete(UUID id);

}