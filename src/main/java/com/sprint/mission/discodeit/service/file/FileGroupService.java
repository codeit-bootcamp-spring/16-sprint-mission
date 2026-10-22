package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.service.GroupService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileGroupService implements GroupService {
    private final File file = new File("data/groups.ser");

    @SuppressWarnings("unchecked")
    private Map<UUID, Group> load() {
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Group>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("그룹 읽기 실패", e);
        }
    }

    private void save(Map<UUID, Group> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("그룹 저장 실패", e);
        }
    }

    @Override
    public Group create(UUID ownerId, String name, String type) {
        Map<UUID, Group> data = load();
        Group group = new Group(ownerId, name, type);
        data.put(group.getId(), group);
        save(data);
        return group;
    }

    @Override
    public Group read(UUID id) {
        return load().get(id);
    }

    @Override
    public List<Group> readAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public Group update(UUID id, String name) {
        Map<UUID, Group> data = load();
        Group group = data.get(id);
        if (group == null) {
            throw new NoSuchElementException("존재하지 않는 Group입니다." + id);
        }
        group.update(name);
        save(data);
        return group;
    }

    @Override
    public List<Group> findByOwnerId(UUID ownerId) {
        return load().values().stream()
                .filter(group -> group.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> findByType(String type) {
        return load().values().stream()
                .filter(group -> group.getType().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public void addItem(UUID groupId, UUID itemId) {
        Map<UUID, Group> data = load();
        Group group = data.get(groupId);
        if (group == null) {
            throw new NoSuchElementException("존재하지 않는 Group입니다." + groupId);
        }
        group.addItem(itemId);
        save(data);
    }

    @Override
    public void removeItem(UUID groupId, UUID itemId) {
        Map<UUID, Group> data = load();
        Group group = data.get(groupId);
        if (group == null) {
            throw new NoSuchElementException("존재하지 않는 Group입니다." + groupId);
        }
        group.removeItem(itemId);
        save(data);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Group> data = load();
        data.remove(id);
        save(data);
    }


}