package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService{

    User create(String username, String email, String password);

    Optional<User> findById(UUID id);

    List<User> findAll();

    Optional<User> findByName(String username);

    Optional<User> updatePw(UUID id, String newPw);

    Optional<User> updateUsername(UUID id, String newUsername);

    Optional<User> updateEmail(UUID id, String newEmail);

    void delete(UUID id);

}
