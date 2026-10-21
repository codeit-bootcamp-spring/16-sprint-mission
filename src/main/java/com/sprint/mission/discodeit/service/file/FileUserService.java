package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = new User(username);

        String fileName = "./data/users/" + user.getId() + ".ser";

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

    @Override
    public List<User> findAll() {
        List<UUID> userIds;
        List<User> users;

        try (Stream<Path> files = Files.list(Path.of("./data/users/"))) {
            userIds = files.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".ser"))
                    .map(name -> name.substring(0, name.length() - 4))
                    .map(UUID::fromString)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        users = userIds.stream()
                .map(this::findById)
                .toList();

        // System.out.println("All users : " + users);

        return users;
    }

    @Override
    public User findById(UUID id) {
        User user = null;

        try (FileInputStream fis = new FileInputStream("./data/users/" + id + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            user = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return user;
    }

    @Override
    public User updateUsername(UUID id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = this.findById(id);
        user.updateUsername(username);

        String fileName = "./data/users/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(user); // 수정된 객체로 덮어쓰기

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            Files.deleteIfExists(Path.of("./data/users/" + id + ".ser"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
