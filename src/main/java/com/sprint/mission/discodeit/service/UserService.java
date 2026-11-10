
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;

public interface UserService {

    User add(User user);

    User getById(UUID id);

    List<User> getAll();

    User update(UUID id, String username);

    void delete(UUID id);

    default UserResponse create(UserCreateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default UserResponse find(UUID id) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default List<UserResponse> findAll() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default UserResponse update(UserUpdateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

}
