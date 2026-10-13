package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // 유저 생성
    User createUser (String name, int age);
    // 유저 정보 id로 조회
    Optional<User> findById(UUID id);
    // 유저 정보 이름으로 조회
    List<User> findByName(String name);
    // 유저 전체 리스트
    List<User> findAll();
    // 유저 정보 수정
    User updateUser (UUID id, String name, int age);
    // 유저 삭제
    void deleteUser (UUID id);
}
