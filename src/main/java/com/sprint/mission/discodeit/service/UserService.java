package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequest request, MultipartFile profile);
    UserDto findById(UUID id);
    List<UserDto> findAll();
    User update(UserUpdateRequest request, MultipartFile profile);
    void delete(UUID id);
}
