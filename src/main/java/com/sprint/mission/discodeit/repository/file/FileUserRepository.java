package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository{
    private final File file = new File("data/users.ser");

@SuppressWarnings("unchecked")
private Map<UUID, User> load() {
    if(!file.exists()) {
        return new HashMap<>();
    }
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
    return (Map<UUID, User>) ois.readObject();
} catch (IOException | ClassNotFoundException e) {
    throw new RuntimeException("파일 읽기 실패",e);
    }
}
private void saveAll(Map<UUID, User> data) {
    file.getParentFile().mkdirs();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
        oos.writeObject(data);
    } catch (IOException e) {
        throw new RuntimeException("파일 저장 실패", e);
    }
}

@Override
public User save(User user) {
    Map<UUID, User> data = load();
    data.put(user.getId(), user);
    saveAll(data);
    return user;
}

@Override
public User findById(UUID id) {
    return load().get(id);
}

@Override
public List<User> findAll() {
    return new ArrayList<>(load().values());
}

@Override
public void deleteById(UUID id) {
    Map<UUID, User> data = load();
    data.remove(id);
    saveAll(data);
}
}