package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {

    User getUserById(UUID userId);

    void createUser(User user);

    void updateUsername(UUID id, String username);

    void updateEmail(UUID id, String email);

    void updatePassword(UUID id, String originPassword, String newPassword);

    void deleteUser(UUID id, String password);

    void deleteEmail(UUID id);
}
