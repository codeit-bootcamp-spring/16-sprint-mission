package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.service.GroupService;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class JCFGroupService implements GroupService{
    private final Map<UUID,Group> data;

    public JCFGroupService() {
        this.data = new HashMap<>();
    }
    @Override
    public Group create(UUID ownerId, String name, String type) {
        Group group = new Group(ownerId, name, type);
        data.put(group.getId(),group);
        return group;
    }

    @Override
    public Group read(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Group> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Group> findByOwnerId(UUID ownerId) {
        return data.values().stream()
        .filter(group -> group.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> findByType(String type) {
        return data.values().stream()
                .filter(group -> group.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public Group update(UUID id, String name) {
        Group group = data.get(id);
        group.update(name);
        return group;
    }
    @Override
    public void addItem(UUID groupId, UUID itemId) {
        Group group = data.get(groupId);
        group.addItem(itemId);
    }

    @Override
    public void removeItem(UUID groupId, UUID itemId) {
        Group group = data.get(groupId);
        group.removeItem(itemId);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

}
