package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.repository.GroupRepository;
import com.sprint.mission.discodeit.service.GroupService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicGroupService implements GroupService {
        private final GroupRepository groupRepository;

        public BasicGroupService(GroupRepository groupRepository) {
            this.groupRepository = groupRepository;
        }

    @Override
    public Group create(UUID ownerId, String name, String type) {
        Group group = new Group(ownerId, name, type);
        return groupRepository.save(group);
    }

    @Override
    public Group read(UUID id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> readAll() {
        return groupRepository.findAll();
    }

    @Override
    public List<Group> findByOwnerId(UUID owerId) {
            return groupRepository.findAll().stream()
                    .filter(group -> group.getOwnerId().equals(owerId))
                    .collect(Collectors.toList());
}

    @Override
    public List<Group> findByType(String type) {
        return groupRepository.findAll().stream()
                .filter(group -> group.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public Group update(UUID id, String name) {
        Group group = groupRepository.findById(id);
        if (group == null) {
            throw new NoSuchElementException("사용하지 않는 그룹입니다: " + id);
        }
        group.update(name);
        return groupRepository.save(group);
    }

    @Override
    public void addItem(UUID groupId, UUID itemId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new NoSuchElementException("존재하지 않는 Group입니다:" + groupId);
        }
        group.addItem(itemId);
        groupRepository.save(group);
    }

    @Override
    public void removeItem(UUID groupId, UUID itemId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new NoSuchElementException("존재하지 않는 Group입니다:" + groupId);
        }
        group.removeItem(itemId);
        groupRepository.save(group);
    }


        @Override
        public void delete(UUID Id) {
            groupRepository.deleteById(Id);
        }

}
