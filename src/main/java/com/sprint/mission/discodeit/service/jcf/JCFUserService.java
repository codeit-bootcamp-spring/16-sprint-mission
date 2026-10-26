package com.sprint.mission.discodeit.service.jcf; // 구현 클래스가 속한 패키지

import com.sprint.mission.discodeit.entity.User; // 사용자 데이터 타입
import com.sprint.mission.discodeit.service.UserService; // 구현할 인터페이스
import java.util.*; // UUID, List, Map 등의 자료형 사용

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    // 생성자: 사용자 데이터를 저장할 공간 생성
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // 사용자 생성
    @Override
    public User create(User user) {

        if (user == null) {
            throw new IllegalArgumentException("사용자는 null일 수 없습니다.");
        }

        if (user.getUsername() == null) {
            throw new IllegalArgumentException("사용자 이름은 null일 수 없습니다.");
        }

        data.put(user.getId(), user);
        return user;
    }

    // ID로 사용자 조회
    @Override
    public User findById(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        return data.get(id);
    }

    // 모든 사용자 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // 사용자 수정
    @Override
    public User update(UUID id, User user) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        if (user == null) {
            throw new IllegalArgumentException("사용자는 null일 수 없습니다.");
        }

        if (user.getUsername() == null) {
            throw new IllegalArgumentException("사용자 이름은 null일 수 없습니다.");
        }

        // 기존 사용자 조회
        User existing = data.get(id);

        if (existing == null) {
            throw new IllegalArgumentException("수정할 사용자가 존재하지 않습니다.");
        }

        // 기존 객체의 사용자 이름만 수정
        existing.update(user.getUsername());

        return existing;
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 사용자가 존재하지 않습니다.");
        }

        data.remove(id);
    }
}
