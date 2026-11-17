package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    List<User> findAll();

    User save(User user);

    void deleteById(UUID userId);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, UUID userId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID userId);
}
