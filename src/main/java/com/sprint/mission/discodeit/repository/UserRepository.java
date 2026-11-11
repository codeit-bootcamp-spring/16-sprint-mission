package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User 데이터를 저장하고 조회하는 방법의 약속입니다.
 * 실제 저장 방식(Map, 파일 등)은 구현 클래스가 정합니다.
 */
public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    boolean existsById(UUID id);

    boolean deleteById(UUID id);
}
