
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    User getById(UUID id);

    List<User> getAll();

    void delete(UUID id);

    default Optional<User> findByUsername(String username) {
        return getAll().stream()
                .filter(user -> Objects.equals(user.getUsername(), username))
                .findFirst();
    }

    default Optional<User> findByEmail(String email) {
        return getAll().stream()
                .filter(user -> Objects.equals(user.getEmail(), email))
                .findFirst();
    }

}
