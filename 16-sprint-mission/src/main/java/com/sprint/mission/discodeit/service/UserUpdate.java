package com.sprint.mission.discodeit.service;

public interface UserUpdate {

    void updatePw(String id, String newPw);

    void updateUsername(String id, String newUsername);

    void updateEmail(String id, String newEmail);
}
