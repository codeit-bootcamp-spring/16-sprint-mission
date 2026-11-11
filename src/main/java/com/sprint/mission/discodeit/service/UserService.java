package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 유저 생성
    UserDto createUser (UserCreateDto dto);
    // 유저 정보 id로 조회
    UserDto findById(UUID id);
    // 유저 정보 이름으로 조회
    UserDto findByUsername(String name);
    // 유저 정보 이메일로 조회
    UserDto findByEmail(String email);
    // 유저 전체 리스트
    List<UserDto> findAll();
    // 유저 정보 수정
    UserDto updateUser (UserUpdateDto dto);
    // 유저 삭제
    void deleteUser (UUID id);
}
