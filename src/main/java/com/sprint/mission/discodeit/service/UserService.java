package com.sprint.mission.discodeit.service; // 서비스 패키지

import com.sprint.mission.discodeit.entity.User; // User 객체 사용
import java.util.List; // 여러 사용자 목록을 반환하기 위해 사용
import java.util.UUID; // 사용자 ID 사용

public interface UserService { // 사용자 서비스 인터페이스

    User create(User user); // 사용자 생성
    User findById(UUID id); // ID로 사용자 한 명 조회
    List<User> findAll(); // 모든 사용자 조회
    User update(UUID id, User user); // 사용자 수정
    void delete(UUID id); // 사용자 삭제

}
