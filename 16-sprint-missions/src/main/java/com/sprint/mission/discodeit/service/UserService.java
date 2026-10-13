package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    // 생성 기능(유저 생성)
    User createUser(User user);

    // 단건 조회 기능(유저 id 조회)
    User readUser(UUID id);

    // 전체 조회 기능
    List<User> readAllUser();

    // 수정 기능(유저 id 및 유저정보 수정)
    User updateUser(UUID id, User user);

    // 삭제 기능(유저 id를 찾아서 유저 삭제)
    User deleteUser(UUID id);
}