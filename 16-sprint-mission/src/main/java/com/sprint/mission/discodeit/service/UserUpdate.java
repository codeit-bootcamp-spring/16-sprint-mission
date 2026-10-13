package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserUpdate {

    User updatePw(UUID id, String newPw);

    User updateUsername(UUID id, String newUsername);

    User updateEmail(UUID id, String newEmail);
}
