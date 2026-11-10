
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final File directory;

    public FileUserService() {
        this.directory = new File("data/users");

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("사용자 폴더를 생성할 수 없습니다.");
        }
    }

    private File getFile(UUID id) {
        return new File(directory, id + ".ser");
    }

    private void save(User user) {
        File file = getFile(user.getId());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(user);
        } catch (IOException e) {
            throw new IllegalStateException("사용자를 저장할 수 없습니다.", e);
        }
    }

    private User load(File file) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return (User) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("사용자를 불러올 수 없습니다.", e);
        }
    }

    @Override
    public User add(User user) {
        save(user);
        return user;
    }

    @Override
    public User getById(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        return load(file);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files == null) {
            throw new IllegalStateException("사용자 폴더를 읽을 수 없습니다.");
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".ser")) {
                users.add(load(file));
            }
        }

        return users;
    }

    @Override
    public User update(UUID id, String username) {
        User user = getById(id);
        user.update(username);
        save(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        if (!file.delete()) {
            throw new IllegalStateException("사용자를 삭제할 수 없습니다.");
        }
    }
}
