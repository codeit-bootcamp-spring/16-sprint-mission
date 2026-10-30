package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new LinkedHashMap<>();

    @Override
    public UserStatus save(UserStatus entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public UserStatus findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}