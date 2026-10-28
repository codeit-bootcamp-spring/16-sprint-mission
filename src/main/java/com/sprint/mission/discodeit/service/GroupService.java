package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Group;

import java.util.List;
import java.util.UUID;

public interface GroupService {
    Group create(UUID ownerId, String name, String type);
    Group read(UUID id);
    List<Group> readAll();
    List<Group> findByOwnerId(UUID ownerId);
    List<Group> findByType(String type);
    Group update(UUID id, String name);
    void addItem(UUID groupId, UUID itemId);
    void removeItem(UUID groupId, UUID itemId);
    void delete(UUID id);
}
