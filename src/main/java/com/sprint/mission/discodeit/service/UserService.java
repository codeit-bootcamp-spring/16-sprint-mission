package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService extends BaseEntityService {
    User create(String name);

    Optional<User> findById(UUID id);
}
