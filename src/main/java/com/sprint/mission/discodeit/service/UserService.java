package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.User.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserCreateResponse create(UserCreateRequest createRequest, UserProfileImageCreateRequest imageCreateRequest);

    UserUpdateResponse update(UserUpdateRequest updateRequest, UserProfileImageUpdateRequest imageUpdateRequest);

    UserFindResponse findById(UUID id);

    List<UserFindResponse> findAll();

    void delete(UUID id);
}
