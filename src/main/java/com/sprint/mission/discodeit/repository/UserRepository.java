package com.sprint.mission.discodeit.repository;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    User findId(UUID id);
    List<User> findAll();
    void deleteId(UUID id);
}
