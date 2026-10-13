package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService extends BaseEntityService<User> {
    User create(String name);
}
