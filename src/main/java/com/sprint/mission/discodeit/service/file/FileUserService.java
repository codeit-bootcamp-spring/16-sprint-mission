package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {

    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = new User(username);

        String fileName = user.getId() + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // System.out.println(user.getId());

        return user;
    }

    public User deseriailize(UUID userId) {
        User user = null;

        try (FileInputStream fis = new FileInputStream(userId + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            user = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        /*
        List<User> userList = this.data.stream()
                .toList();

        return userList;
        */
        return users;
    }

    @Override
    public User findById(UUID id) {
        User user = this.deseriailize(id);

        return user;
    }

    @Override
    public User updateUsername(UUID id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = this.findById(id);
        user.updateUsername(username);

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        // User user = this.findById(id);
        // this.data.remove(user);
    }
}
