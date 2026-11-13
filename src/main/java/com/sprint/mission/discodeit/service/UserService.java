package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserCreateResponse create(UserCreateRequest createRequest, MultipartFile profileImage);

    UserUpdateResponse update(UUID userId, UserUpdateRequest updateRequest, MultipartFile profileImage);

    UserFindResponse findById(UUID id);

    List<UserFindResponse> findAll();

    void delete(UUID id);
}
