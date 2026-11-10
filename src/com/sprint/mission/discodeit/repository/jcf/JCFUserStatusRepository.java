package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        if (userStatus == null) {
            throw new IllegalArgumentException("사용자 접속 상태 정보는 필수입니다.");
        }

        if (userStatus.getId() == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("사용자 접속 상태를 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}