package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.repository.GroupRepository;

import java.io.*;
import java.util.*;

public class FileGroupRepository implements GroupRepository {
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
    private void saveAll(Map<UUID, Group> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("그룹 저장 실패", e);
        }
    }

    @Override
    public Group save(Group group) {
        Map<UUID, Group> data = load();
        data.put(group.getId(), group);
        saveAll(data);
        return group;
    }

    @Override
    public Group findById(UUID id) {
        return load().get(id);
    }

    @Override
    public List<Group> findAll() {
        return new ArrayList<>(load().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Group> data = load();
        data.remove(id);
        saveAll(data);
    }
}