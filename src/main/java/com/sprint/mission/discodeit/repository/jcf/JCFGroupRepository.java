package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.repository.GroupRepository;

import java.util.*;

public class JCFGroupRepository implements GroupRepository {
    private final Map<UUID, Group> data = new HashMap<>();

    @Override
    public Group save(Group group) {
        data.put(group.getId(), group);
        return group;
    }

    @Override
    public Group findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
