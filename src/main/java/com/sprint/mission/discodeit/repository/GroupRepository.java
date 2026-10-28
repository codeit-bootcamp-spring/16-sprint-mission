package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface GroupRepository {
    Group save(Group group);
    Group findById(UUID id);
    List<Group> findAll();
    void deleteById(UUID id);
}