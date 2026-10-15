package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserService implements UserService {

    private static final String FILE_PATH = "temp/users.ser";

    private final Map<UUID, User> data;

    public FileUserService() throws IOException {
        this.data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, User> loaded = (Map<UUID, User>) findObject;
            data.putAll(loaded);
        } catch (FileNotFoundException e) {
            Path file = Path.of(FILE_PATH);
            Files.createDirectories(file.getParent());
            Files.createFile(file);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        save();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String userName, String email, String password) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다.");
        }
        user.update(userName, email, password);
        save();
        return user;
    }

    @Override
    public void delete(UUID id) {
        if (data.remove(id) == null) {
            throw new NoSuchElementException("존재하지 않는 User입니다.");
        }
        save();
    }

    private void save() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
