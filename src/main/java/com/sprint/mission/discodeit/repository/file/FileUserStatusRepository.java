package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final Path DIRECTORY =
            Paths.get(
                    System.getProperty("user.dir"),
                    "data",
                    "userStatuses"
            );

    public FileUserStatusRepository() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UserStatus load(Path filePath) {
        try (
                FileInputStream fis =
                        new FileInputStream(filePath.toFile());
                ObjectInputStream ois =
                        new ObjectInputStream(fis)
        ) {
            return (UserStatus) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path filePath =
                DIRECTORY.resolve(userStatus.getId() + ".ser");

        try (
                FileOutputStream fos =
                        new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos =
                        new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        try (var paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.toString().endsWith(".ser"))
                    .map(this::load)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path filePath =
                DIRECTORY.resolve(id + ".ser");

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        return Optional.of(load(filePath));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(userStatus ->
                        userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void delete(UserStatus userStatus) {
        Path filePath =
                DIRECTORY.resolve(userStatus.getId() + ".ser");

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
