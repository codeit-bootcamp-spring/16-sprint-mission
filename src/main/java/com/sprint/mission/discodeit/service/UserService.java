package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 생성
    User createUser(User user);

    // 조회
    User getUserById(UUID userId);
    List<User> getAllUsers();

    // 수정
    User updateUsername(UUID id, String username);
    User updatePassword(UUID id, String originPassword, String newPassword);
    User updateEmail(UUID id, String email);

    // 삭제
    void deleteUser(UUID id, String password);
    void deleteEmail(UUID id);

}
