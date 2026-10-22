package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        User user = new User(username);

        /*
        String fileName = "./data/users/" + user.getId() + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException exception) {
            throw new UncheckedIOException("사용자 저장에 실패했습니다.", exception);
        }

        return user;
         */

        this.userRepository.save(user);

        return user;

    }

    @Override
    public List<User> findAll() {
        /*
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
            throw new UncheckedIOException("사용자 목록 조회에 실패했습니다.", exception);
        }

        users = userIds.stream()
                .map(this::findById)
                .toList();
        */

        List<User> users = this.userRepository.getAllUsers();

        return users;
    }

    @Override
    public User findById(UUID id) {
        /*
        User user = null;

        try (FileInputStream fis = new FileInputStream("./data/users/" + id + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            user = (User) ois.readObject();
        } catch (NoSuchFileException exception) {
            throw new NoSuchElementException("사용자가 없습니다: " + id);
        } catch (IOException exception) {
            throw new UncheckedIOException("사용자 조회에 실패했습니다.", exception);
        } catch(ClassNotFoundException exception) {
            throw new IllegalStateException("사용자 역직렬화에 실패했습니다.", exception);
        }

        return user;
         */
        User user = this.userRepository.getUser(id);

        return user;
    }

    @Override
    public User updateUsername(UUID id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        User user = this.findById(id);
        user.updateUsername(username);

        String fileName = "./data/users/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(user);

        } catch (IOException exception) {
            throw new UncheckedIOException("사용자 업데이트에 실패했습니다.", exception);
        }

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            Files.deleteIfExists(Path.of("./data/users/" + id + ".ser"));
        } catch (IOException exception) {
            throw new UncheckedIOException("사용자 삭제에 실패했습니다.", exception);
        }
    }
}
