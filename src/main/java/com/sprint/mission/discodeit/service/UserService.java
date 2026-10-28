package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /** 기존 객체 기반 API: User 객체를 서비스에 전달해 등록합니다. */
    void create(User user);

    /** 템플릿용 편의 API: 입력값으로 User를 만들고 등록한 뒤 생성된 객체를 돌려줍니다. */
    default User create(String username, String email, String password) {
        User user = new User(username, email, password);
        create(user);
        return user;
    }

    User findById(UUID id);

    List<User> findAll();

    void update(UUID id, User user);

    void delete(UUID id);
}
