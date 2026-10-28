package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // 생성 + 수정
    User save(User user);
    // 단건 조회
    Optional<User> findByUserId(UUID id);
    // 전체 조회
    List<User> findAll();
    // 삭제
    void deleteByUserId(UUID id);
    // 존재 여부 검사
    boolean existsByUserId(UUID id);
}