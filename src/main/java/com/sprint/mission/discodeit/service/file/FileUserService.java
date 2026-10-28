package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService{
    private final File file = new File("data/users.ser");

    @SuppressWarnings("unchecked")
    private Map<UUID,User> load() {
        if(!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        }
    }

    private void save(Map<UUID, User> data) {
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
            public User create(String username, String password, String phoneNumber, String email) {
            Map<UUID, User>data = load();
            User user = new User(username,password,phoneNumber,email);
                data.put(user.getId(), user);
                save(data);
                return user;
            }
            @Override
            public User read(UUID id) {
                return load().get(id);
            }
            @Override
            public List<User> readAll() {
                return new ArrayList<>(load().values());
            }
            @Override
            public User update(UUID id, String username, String password, String phoneNumber, String email) {
                Map<UUID, User> data = load();
                User user = data.get(id);
                if (user == null) {
                    throw new NoSuchElementException("존재하지 않는 User입니다." + id);
                }
                user.update(username, password, phoneNumber, email);
                save(data);
                return user;
            }
            @Override
            public void delete(UUID id) {
                Map<UUID, User> data = load();
                data.remove(id);
                save(data);
            }


}
