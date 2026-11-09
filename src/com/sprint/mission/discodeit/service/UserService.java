package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    // 기존 방식 유지
    UserDto create(UserCreateRequest request);

    // MultipartFile을 이용한 회원가입
    UserDto create(UserCreateRequest request, MultipartFile profile);

    Optional<UserDto> findById(UUID id);

    List<UserDto> findAll();

    UserDto update(UserUpdateRequest request);

    void deleteById(UUID id);

    void addChannel(UUID userId, UUID channelId);

    void removeChannel(UUID userId, UUID channelId);
}