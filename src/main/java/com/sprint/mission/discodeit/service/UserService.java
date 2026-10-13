package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {
    boolean createUser(User user);

    boolean updateUsername(UUID id, String username);

    boolean updateEmail(UUID id, String email);

    boolean updatePassword(UUID id, String originPassword, String newPassword);

    boolean deleteUser(UUID id, String password);

    User getUserById(UUID id);

}
