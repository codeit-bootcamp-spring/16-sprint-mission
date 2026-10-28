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
    /** ID가 같은 데이터를 새로 저장하거나 기존 데이터 위에 저장합니다. */
    void save(User user);

    /** ID로 찾습니다. 없을 수 있으므로 Optional로 결과를 표현합니다. */
    Optional<User> findById(UUID id);

    /** 저장된 모든 User를 목록으로 돌려줍니다. */
    List<User> findAll();

    /** 해당 ID의 데이터가 있는지만 확인합니다. */
    boolean existsById(UUID id);

    /** ID에 해당하는 데이터를 지웁니다. 지웠으면 true를 반환합니다. */
    boolean deleteById(UUID id);
}
