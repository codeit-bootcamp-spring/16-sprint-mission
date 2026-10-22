package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    public FileUserRepository() {
        this.writeUserFile(new HashMap<>());
    }

    @Override
    public User save(User user) {
        Map<UUID, User> users = this.readUserFile();
        users.put(user.getId(), user);
        this.writeUserFile(users);
        return user;
    }

    @Override
    public User update(UUID id, User newUser) {
        Map<UUID, User> users = this.readUserFile();
        users.put(id, newUser);
        this.writeUserFile(users);
        return newUser;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Map<UUID, User> users = this.readUserFile();
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> users = this.readUserFile();
        return users.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> users = this.readUserFile();
        User user = users.get(id);
        user.softDelete();
        this.update(id, user);
    }

    public Map<UUID, User> readUserFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/user.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, User> users = (Map<UUID, User>) ois.readObject();
            return users;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeUserFile(Map<UUID, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/user.ser"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }
}

