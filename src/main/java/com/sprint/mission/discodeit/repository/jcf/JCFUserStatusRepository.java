
package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public void save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
    }

    @Override
    public UserStatus getById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<UserStatus> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return Optional.of(userStatus);
            }
        }

        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
