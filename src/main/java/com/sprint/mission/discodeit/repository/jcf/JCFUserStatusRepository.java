package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {

    // 유저 ID를 키로 사용하여 유저 상태를 관리하는 Map
    private final Map<UUID, UserStatus> data;

    // 유저 상태를 저장할 HashMap을 초기화한다.
    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(UserStatus userStatus) {
        // 유저 ID를 키로 사용하여 저장하거나 수정한다.
        data.put(userStatus.getUserId(), userStatus);
    }

    // ID로 UserStatus 조회
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.values().stream()
                .filter(userStatus -> userStatus.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        // 유저 ID가 일치하는 UserStatus를 찾아 반환한다.
        // 일치하는 데이터가 없으면 Optional.empty()를 반환한다.
        return Optional.ofNullable(data.get(userId));
    }

    // 모든 UserStatus 조회
    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        // 유저 ID가 일치하는 UserStatus를 삭제한다.
        UserStatus removed = data.remove(userId);

        // 삭제된 데이터가 없으면 예외를 발생시킨다.
        if (removed == null) {
            throw new IllegalArgumentException("삭제할 UserStatus를 찾을 수 없습니다.");
        }
    }

    // ID로 UserStatus 삭제
    @Override
    public void deleteById(UUID id) {
        UserStatus userStatus = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 UserStatus를 찾을 수 없습니다."));

        data.remove(userStatus.getUserId(), userStatus);
    }
}