package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.users.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.users.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 생성 기능(유저 생성)
    UserDto createUser(UserCreateRequest request, MultipartFile profile);

    // 단건 조회 기능(유저 id 조회)
    UserDto readUser(UUID id);

    // 전체 조회 기능
    List<UserDto> readAllUser();

    // 수정 기능(유저 정보 및 프로필 이미지 수정)
    UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile profile);

    // 삭제 기능(유저 id를 찾아서 관련 데이터와 함께 삭제)
    void deleteUser(UUID id);
}