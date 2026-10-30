package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(String username, String email, String password);

    User findById(UUID id);

    List<User> findAll();

    User update(
            UUID id,
            String username,
            String email,
            String password
    );

    void delete(UUID id);

    void save(User user);

    default UserResponse create(CreateUserRequest request) {
        User user = create(
                request.username(),
                request.email(),
                request.password()
        );

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                false,
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    default UserResponse findResponseById(UUID id) {
        User user = findById(id);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                false,
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    default List<UserResponse> findAllResponses() {
        return findAll().stream()
                .map(user -> findResponseById(user.getId()))
                .toList();
    }

    default UserResponse update(UpdateUserRequest request) {
        update(
                request.id(),
                request.username(),
                request.email(),
                request.password()
        );

        return findResponseById(request.id());
    }
}